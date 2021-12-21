package done.advent2021;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class PacketDecoder {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            String line = in.next();
            System.out.println("Line: " + line);
            String bits = line.chars().mapToObj(BITS::fromHex).collect(joining());
            System.out.println("BITS: " + bits);

            PacketParser packetParser = new PacketParser(bits);
            Packet packet = packetParser.packet();

            System.out.println("Packet: " + packet);

            long result = packet.calculate();

            System.out.println("Result: " + result);
        }
    }

    record BITS() {
        static String fromHex(int hex) {
            return switch(hex) {
                case '0' -> "0000";
                case '1' -> "0001";
                case '2' -> "0010";
                case '3' -> "0011";
                case '4' -> "0100";
                case '5' -> "0101";
                case '6' -> "0110";
                case '7' -> "0111";
                case '8' -> "1000";
                case '9' -> "1001";
                case 'A' -> "1010";
                case 'B' -> "1011";
                case 'C' -> "1100";
                case 'D' -> "1101";
                case 'E' -> "1110";
                case 'F' -> "1111";
                default -> throw new IllegalArgumentException("Invalid hex character: " + hex);
            };
        }
    }

    record OperatorPacket(int version, TypeID typeID, Length lenghtID, List<Packet> packets) implements Packet {
        @Override
        public int length() {
            return 7 + lenghtID.bits() + packets.stream().mapToInt(Packet::length).sum();
        }

        @Override
        public int sumVersion() {
            return version + packets.stream().mapToInt(Packet::sumVersion).sum();
        }

        @Override
        public long calculate() {
            return switch (typeID) {
                case SUM -> calculatePackets().sum();
                case PRODUCT -> calculatePackets().reduce((a, b) -> a * b).orElse(0L);

                case MAX -> calculatePackets().max().orElse(0L);
                case MIN -> calculatePackets().min().orElse(0L);

                case GREATER -> calculatePackets().limit(2).reduce((a, b) -> a > b ? 1 : 0).orElse(0L);
                case LESS -> calculatePackets().limit(2).reduce((a, b) -> a < b ? 1 : 0).orElse(0L);
                case EQUAL -> calculatePackets().limit(2).reduce((a, b) -> a == b ? 1 : 0).orElse(0L);

                default -> throw new IllegalArgumentException();
            };
        }

        private LongStream calculatePackets() {
            return packets.stream().mapToLong(Packet::calculate);
        }
    }

    record LiteralPacket(int version, int bits, long value) implements Packet {

        @Override
        public int length() {
            return bits + 6;
        }

        @Override
        public int sumVersion() {
            return version;
        }

        @Override
        public long calculate() {
            return value;
        }
    }

    record NullPacket(int bits) implements Packet {

        @Override
        public int length() {
            return bits;
        }

        @Override
        public int sumVersion() {
            return 0;
        }

        @Override
        public long calculate() {
            return 0L;
        }
    }

    interface Packet {
        int length();
        int sumVersion();
        long calculate();
    }

    enum Length {
        LEN_11(11),
        LEN_15(15),
        ;

        private final int bits;

        Length(int bits) {
            this.bits = bits;
        }

        int bits() {
            return bits;
        }

        static Length valueOf(char id) {
            return id == '1' ? Length.LEN_11 : Length.LEN_15;
        }
    }

    enum TypeID {
        SUM(0),
        PRODUCT(1),

        MIN(2),
        MAX(3),

        LITERAL(4),

        GREATER(5),
        LESS(6),
        EQUAL(7),
        ;

        private final int code;

        TypeID(int code) {
            this.code = code;
        }

        static TypeID valueOf(int code) {
            return Stream.of(values()).filter(t -> t.code == code).findFirst().orElseThrow();
        }
    }

    static class PacketParser {
        private final String input;
        private int offset = 0;

        PacketParser(String input) {
            this.input = input;
        }

        Packet packet() {
            return packet(input.length());
        }

        Packet packet(int limit) {
            if (offset + 6 >= limit) {
                return nullPacket(limit);
            }

            int version = readVersion();
            int typeID = readTypeID();
            if (isLiteral(typeID)) {
                return literal(version);
            }
            return operator(version, typeID);
        }

        private Packet nullPacket(int limit) {
            int bits = limit - offset;
            offset = limit;
            return new NullPacket(bits);
        }

        private LiteralPacket literal(int version) {
            int startOffset = offset;
            long value = readLiteral();
            int bits = offset - startOffset;
            return new LiteralPacket(version, bits, value);
        }

        private OperatorPacket operator(int version, int typeID) {
            Length lenghtID = readLengthID();
            int length = readLength(lenghtID);
            int bits = getBits(lenghtID, length);
            int limit = bits > 0 ? offset + bits : input.length();
            List<Packet> packets = new ArrayList<>();
            int processed = 0;
            while (true) {
                Packet packet = packet(limit);
                packets.add(packet);
                processed += packet.length();
                if (bits == 0 && packets.size() == length) {
                    break;
                }
                if (bits > 0 && processed >= length) {
                    break;
                }
            }
            return new OperatorPacket(version, TypeID.valueOf(typeID), lenghtID, packets);
        }

        private int getBits(Length lenghtID, int length) {
            return lenghtID == Length.LEN_15 ? length : 0;
        }

        private int readVersion() {
            int version = bitsToInt(input.substring(offset, offset + 3));
            offset += 3;
            return version;
        }

        private int readTypeID() {
            int typeID = bitsToInt(input.substring(offset, offset + 3));
            offset += 3;
            return typeID;
        }

        private Length readLengthID() {
            return Length.valueOf(input.charAt(offset++));
        }

        private int readLength(Length length) {
            String lenStr = input.substring(offset, offset + length.bits);
            offset += length.bits;
            return Integer.valueOf(lenStr, 2);
        }

        private Long readLiteral() {
            StringBuilder buffer = new StringBuilder();
            boolean hasNext = true;
            while (hasNext) {
                String chunk = readLiteralPart();
                buffer.append(chunk.substring(1));
                hasNext = chunk.charAt(0) == '1';
            }
            return Long.valueOf(buffer.toString(), 2);
        }

        private String readLiteralPart() {
            String chunk = input.substring(offset, offset + 5);
            offset += 5;
            return chunk;
        }

        private boolean isLiteral(int typeID) {
            return typeID == 4;
        }

        private int bitsToInt(String bits) {
            return Integer.valueOf(bits, 2);
        }
    }

    @SuppressWarnings("unused")
    private static final String SIMPLE1 = """
            D2FE28""";

    @SuppressWarnings("unused")
    private static final String INPUT1 = """
            8A004A801A8002F478""";

    @SuppressWarnings("unused")
    private static final String INPUT2 = """
            620080001611562C8802118E34""";

    @SuppressWarnings("unused")
    private static final String INPUT3 = """
            C0015000016115A2E0802F182340""";

    @SuppressWarnings("unused")
    private static final String INPUT4 = """
            A0016C880162017C3686B18A3D4780""";

    @SuppressWarnings("unused")
    private static final String INPUT5 = """
            CE00C43D881120""";

    @SuppressWarnings("unused")
    private static final String INPUT6 = """
            04005AC33890""";

    @SuppressWarnings("unused")
    private static final String INPUT7 = """
            9C0141080250320F1802104A08""";

    @SuppressWarnings("unused")
    private static final String INPUT = """
            20546718027401204FE775D747A5AD3C3CCEEB24CC01CA4DFF2593378D645708A56D5BD704CC0110C469BEF2A4929689D1006AF600AC942B0BA0C942B0BA24F9DA8023377E5AC7535084BC6A4020D4C73DB78F005A52BBEEA441255B42995A300AA59C27086618A686E71240005A8C73D4CF0AC40169C739584BE2E40157D0025533770940695FE982486C802DD9DC56F9F07580291C64AAAC402435802E00087C1E8250440010A8C705A3ACA112001AF251B2C9009A92D8EBA6006A0200F4228F50E80010D8A7052280003AD31D658A9231AA34E50FC8010694089F41000C6A73F4EDFB6C9CC3E97AF5C61A10095FE00B80021B13E3D41600042E13C6E8912D4176002BE6B060001F74AE72C7314CEAD3AB14D184DE62EB03880208893C008042C91D8F9801726CEE00BCBDDEE3F18045348F34293E09329B24568014DCADB2DD33AEF66273DA45300567ED827A00B8657B2E42FD3795ECB90BF4C1C0289D0695A6B07F30B93ACB35FBFA6C2A007A01898005CD2801A60058013968048EB010D6803DE000E1C6006B00B9CC028D8008DC401DD9006146005980168009E1801B37E02200C9B0012A998BACB2EC8E3D0FC8262C1009D00008644F8510F0401B825182380803506A12421200CB677011E00AC8C6DA2E918DB454401976802F29AA324A6A8C12B3FD978004EB30076194278BE600C44289B05C8010B8FF1A6239802F3F0FFF7511D0056364B4B18B034BDFB7173004740111007230C5A8B6000874498E30A27BF92B3007A786A51027D7540209A04821279D41AA6B54C15CBB4CC3648E8325B490401CD4DAFE004D932792708F3D4F769E28500BE5AF4949766DC24BB5A2C4DC3FC3B9486A7A0D2008EA7B659A00B4B8ACA8D90056FA00ACBCAA272F2A8A4FB51802929D46A00D58401F8631863700021513219C11200996C01099FBBCE6285106""";
}