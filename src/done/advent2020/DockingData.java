package done.advent2020;

import org.junit.jupiter.api.Test;
import util.LineSupplier;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DockingData {

    static Supplier<MemoryMaskFunction> mask;
    static Map<BigInteger, Long> mem = new HashMap<>();

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            mask = parseMask(in.nextLine());
            Stream.generate(new LineSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .forEach(DockingData::updateMemory);

            long result = mem.values().stream().mapToLong(Long::longValue).sum();

            System.out.println("Result: " + result);
        }
    }

    private static void updateMemory(String line) {
        if (line.startsWith("mask")) {
            mask = parseMask(line);
        } else {
            final Update update = Update.toUpdate(line);
            System.out.println("Update: " + update);
            update.updateMem();
        }
    }

    private static Supplier<MemoryMaskFunction> parseMask(String line) {
        int eq = line.indexOf('=') + 1;
        String maskLine = line.substring(eq).trim();

        System.out.println("mask: " + maskLine);
        return () -> addr -> {
            Stream<BigInteger> result = Stream.of(addr);
            for (int i = 0; i < maskLine.length(); i++) {
                final char c = maskLine.charAt(i);
                final int idx = maskLine.length() - i - 1;
                result = result.flatMap(value ->
                    switch (c) {
                        case '1' -> Stream.of(value.setBit(idx));
                        case 'X' -> Stream.of(value.setBit(idx), value.clearBit(idx));
                        default -> Stream.of(value);
                    }
                );
            }
            return result;
        };
    }

    static class Update {
        final BigInteger addr;
        final long value;

        Update(BigInteger addr, long value) {
            this.addr = addr;
            this.value = value;
        }

        void updateMem() {
            mask.get().apply(addr).forEach(memAddr -> {
                System.out.println("update: mem[" + memAddr + "] = " + value);
                mem.put(memAddr, value);
            });
        }

        @Override
        public String toString() {
            return "mem[" + addr + "] = " + value;
        }

        static Update toUpdate(String line) {
            int start = line.indexOf('[') + 1;
            int end = line.indexOf(']');
            BigInteger addr = new BigInteger(line.substring(start, end));

            int eq = line.indexOf('=') + 1;
            long value = Long.parseLong(line.substring(eq).trim());
            return new Update(addr, value);
        }
    }

    interface MemoryMaskFunction extends Function<BigInteger, Stream<BigInteger>> {

    }

    private static final String INPUT = "" +
            "mask = 000000000000000000000000000000X1001X\n" +
            "mem[42] = 100\n" +
            "mask = 00000000000000000000000000000000X0XX\n" +
            "mem[26] = 1\n";

    private static final String INPUT2 = "" +
            "mask = 00101X10011X0X111110010X010011X10101\n" +
            "mem[41248] = 4595332\n" +
            "mem[26450] = 60\n" +
            "mem[32210] = 982366\n" +
            "mem[1060] = 234632920\n" +
            "mem[20694] = 38159\n" +
            "mem[45046] = 58906955\n" +
            "mask = 010110010X1101XX11X0100001X0000X00X1\n" +
            "mem[16069] = 7758\n" +
            "mem[55864] = 2473265\n" +
            "mem[37095] = 103513009\n" +
            "mem[4911] = 1002\n" +
            "mem[63231] = 6932274\n" +
            "mem[21265] = 72322159\n" +
            "mem[43724] = 16591353\n" +
            "mask = 01001X01X101011101010101011X1X000000\n" +
            "mem[63470] = 30339812\n" +
            "mem[16920] = 471738\n" +
            "mem[1014] = 29735753\n" +
            "mem[61061] = 6866\n" +
            "mem[8437] = 9138168\n" +
            "mem[46487] = 1819945\n" +
            "mem[2985] = 15040783\n" +
            "mask = 0X10X1101111001X1X100X1X00011100XX11\n" +
            "mem[32836] = 12902\n" +
            "mem[60365] = 24782\n" +
            "mem[29953] = 10085\n" +
            "mem[18214] = 1160\n" +
            "mask = 001011X10X11100000100X0X0X0X01011001\n" +
            "mem[39434] = 37383633\n" +
            "mem[278] = 670174555\n" +
            "mem[34062] = 20749996\n" +
            "mem[2583] = 6222093\n" +
            "mask = 01X111X1001101X11110100XX001X1000XX1\n" +
            "mem[6075] = 49890\n" +
            "mem[9363] = 2392780\n" +
            "mem[24967] = 218861\n" +
            "mask = X110111X1XX1010101111X01XX1000X001X1\n" +
            "mem[41334] = 11836\n" +
            "mem[24242] = 7263066\n" +
            "mem[17289] = 64986060\n" +
            "mem[2583] = 4702503\n" +
            "mem[21650] = 103905\n" +
            "mem[134] = 486675\n" +
            "mask = 00X010100110XXXX111000XXX1000011000X\n" +
            "mem[45307] = 37940\n" +
            "mem[16597] = 224911\n" +
            "mem[17943] = 392744\n" +
            "mem[55001] = 622484\n" +
            "mem[35954] = 470\n" +
            "mask = 11X01011X11000X1X1100X100X011101X011\n" +
            "mem[1005] = 56755\n" +
            "mem[16146] = 4333571\n" +
            "mem[32347] = 10486693\n" +
            "mem[11452] = 377363\n" +
            "mem[25158] = 328161913\n" +
            "mem[51956] = 250388\n" +
            "mem[10044] = 34078606\n" +
            "mask = 011011X1X111010111110000X001X1X00110\n" +
            "mem[8773] = 10575925\n" +
            "mem[33116] = 175\n" +
            "mem[36101] = 14593\n" +
            "mask = 0100010X110X0101010XX10X011111XX1101\n" +
            "mem[21083] = 1922\n" +
            "mem[3653] = 912\n" +
            "mem[26768] = 7321934\n" +
            "mem[49134] = 17616\n" +
            "mem[62950] = 41565481\n" +
            "mem[12957] = 2136786\n" +
            "mem[10324] = 17788\n" +
            "mask = X11X0X0X11010101110X01111010X1100X11\n" +
            "mem[5462] = 18755\n" +
            "mem[39408] = 2435211\n" +
            "mem[49271] = 6589\n" +
            "mask = X1X011XX01X100010110001X0X0X111X1100\n" +
            "mem[52570] = 2166\n" +
            "mem[28731] = 16573421\n" +
            "mem[18265] = 1192\n" +
            "mem[22435] = 10856992\n" +
            "mem[19263] = 7550\n" +
            "mem[30541] = 434738\n" +
            "mem[36101] = 869138\n" +
            "mask = 010001X001010001XX010100000010110X01\n" +
            "mem[52893] = 125505223\n" +
            "mem[22919] = 597\n" +
            "mem[62950] = 54107\n" +
            "mem[52797] = 7649588\n" +
            "mem[30421] = 3968\n" +
            "mem[30429] = 614720\n" +
            "mask = 01X0X10001X100010X1011XX00000X111X00\n" +
            "mem[44718] = 11141064\n" +
            "mem[42713] = 206218234\n" +
            "mem[51781] = 527553473\n" +
            "mem[1967] = 27527823\n" +
            "mem[6386] = 5404\n" +
            "mask = 00101X10XX11X0XX1110001000001110X11X\n" +
            "mem[62339] = 72046594\n" +
            "mem[14657] = 3243652\n" +
            "mem[750] = 40239\n" +
            "mem[134] = 1936539\n" +
            "mem[5775] = 266384125\n" +
            "mask = 011X111XXX110X01X11X000X00010100011X\n" +
            "mem[2956] = 438895\n" +
            "mem[41520] = 7282\n" +
            "mem[42192] = 34769\n" +
            "mem[8837] = 2587\n" +
            "mask = 01XX11100101000X0X10011XX01010011101\n" +
            "mem[12515] = 450388\n" +
            "mem[62175] = 649233\n" +
            "mem[54743] = 129273\n" +
            "mem[10284] = 159823\n" +
            "mem[31311] = 16983\n" +
            "mem[56137] = 852771967\n" +
            "mask = 11010X1X01010101X1010X11101111X00010\n" +
            "mem[47190] = 526627409\n" +
            "mem[34299] = 540572\n" +
            "mem[61226] = 61426238\n" +
            "mem[12892] = 61446\n" +
            "mem[33421] = 4192\n" +
            "mask = 0110111111X10101111010100XX01XX10100\n" +
            "mem[41685] = 258\n" +
            "mem[26983] = 60795579\n" +
            "mem[28064] = 10483\n" +
            "mem[33070] = 66557269\n" +
            "mem[12624] = 448724\n" +
            "mem[38125] = 141175913\n" +
            "mask = 010X1X00X101000X0111010101XX01011000\n" +
            "mem[12957] = 7693971\n" +
            "mem[45285] = 4628\n" +
            "mem[48546] = 799\n" +
            "mem[17857] = 7578026\n" +
            "mask = 00101X100101X0010110000000XX1010X110\n" +
            "mem[41841] = 234511\n" +
            "mem[27387] = 2990\n" +
            "mem[24636] = 1269957\n" +
            "mem[15638] = 428392\n" +
            "mem[22064] = 272\n" +
            "mask = 0XXX10X01011X011111000000XX0X100X010\n" +
            "mem[26764] = 482715793\n" +
            "mem[8422] = 70439\n" +
            "mem[17857] = 28381730\n" +
            "mem[4524] = 750659820\n" +
            "mask = 11101100010100X1011000111000XX00X010\n" +
            "mem[52570] = 517468200\n" +
            "mem[25263] = 11113122\n" +
            "mem[33421] = 32762600\n" +
            "mask = 11101X01XX1000010X10111000X1101X0X00\n" +
            "mem[16577] = 910\n" +
            "mem[32450] = 16924479\n" +
            "mem[4421] = 24801362\n" +
            "mem[46638] = 8546454\n" +
            "mask = 01X11X1101110101X1X1X010000XX101X001\n" +
            "mem[34209] = 24703796\n" +
            "mem[30481] = 831\n" +
            "mem[46487] = 147322\n" +
            "mem[38619] = 11686\n" +
            "mem[26615] = 1174\n" +
            "mask = 010X0X00110100X1XX000010110XX100X001\n" +
            "mem[53587] = 198046\n" +
            "mem[38420] = 22334\n" +
            "mem[20181] = 962\n" +
            "mask = XX101101X01000010XX01111001111010100\n" +
            "mem[33812] = 107321\n" +
            "mem[8613] = 7395\n" +
            "mem[1117] = 149990\n" +
            "mem[22919] = 23596\n" +
            "mask = 1X01110110010X01X100000001111011X010\n" +
            "mem[57800] = 254591077\n" +
            "mem[6633] = 60308580\n" +
            "mem[8980] = 104196938\n" +
            "mem[5936] = 289911936\n" +
            "mem[44806] = 297364592\n" +
            "mask = 11X10XX0X1010X01010110XXX01111100X00\n" +
            "mem[49271] = 177794\n" +
            "mem[15368] = 259266583\n" +
            "mem[19327] = 590\n" +
            "mem[40243] = 24245\n" +
            "mem[57130] = 1201404\n" +
            "mem[22545] = 1831196\n" +
            "mem[59161] = 25210381\n" +
            "mask = 0X101X11111X010111100X110XX11000X10X\n" +
            "mem[38749] = 2091454\n" +
            "mem[45138] = 621877\n" +
            "mem[52107] = 3430339\n" +
            "mask = 0010X110X11X00101X100011XX111X000100\n" +
            "mem[17228] = 252642\n" +
            "mem[23892] = 13721\n" +
            "mem[43787] = 2786942\n" +
            "mem[55481] = 58875\n" +
            "mem[513] = 892\n" +
            "mem[62445] = 40312\n" +
            "mask = 0010X11XXX11001011X01010X0111110X100\n" +
            "mem[17415] = 7415167\n" +
            "mem[9048] = 46059\n" +
            "mem[2159] = 636711036\n" +
            "mask = X010111X111X010X1110X10100XX1000X00X\n" +
            "mem[38420] = 104527\n" +
            "mem[24790] = 85\n" +
            "mem[58634] = 127952377\n" +
            "mem[8958] = 11672057\n" +
            "mask = X01X111X00X110XX0X10000000000X0X0100\n" +
            "mem[283] = 241\n" +
            "mem[8898] = 36719\n" +
            "mem[49134] = 217820\n" +
            "mem[31884] = 419937\n" +
            "mask = 0XX11110X1110X0101111000000100110X00\n" +
            "mem[27694] = 6848\n" +
            "mem[25843] = 331711\n" +
            "mem[6688] = 581239\n" +
            "mem[41591] = 171\n" +
            "mask = 0100X100X1010X01010X001XX01XX1010101\n" +
            "mem[30429] = 1103121\n" +
            "mem[42192] = 7844667\n" +
            "mem[21668] = 51727200\n" +
            "mask = 001X1X10001X101XX1100X1000101100X010\n" +
            "mem[4322] = 157863993\n" +
            "mem[49962] = 9140\n" +
            "mem[16964] = 1599\n" +
            "mem[14443] = 2038\n" +
            "mem[3767] = 16636129\n" +
            "mem[13476] = 485497191\n" +
            "mem[1663] = 163345\n" +
            "mask = X101110111010101X1X10011001X10110000\n" +
            "mem[13172] = 195\n" +
            "mem[33921] = 5684133\n" +
            "mem[1337] = 51317\n" +
            "mask = X1XXX101110101X1010X0100XX111X101001\n" +
            "mem[63928] = 4636\n" +
            "mem[56436] = 3887978\n" +
            "mem[6185] = 3037\n" +
            "mem[7095] = 11521156\n" +
            "mem[1663] = 121401\n" +
            "mem[7218] = 20750\n" +
            "mask = 010001001X0101010X00001XXX100XX10100\n" +
            "mem[24149] = 309519\n" +
            "mem[16287] = 12731276\n" +
            "mem[29772] = 65227\n" +
            "mem[37172] = 2824\n" +
            "mem[17508] = 59271\n" +
            "mem[22133] = 3806\n" +
            "mask = 01X0X101011X000X0X101000100011111101\n" +
            "mem[14401] = 158547520\n" +
            "mem[37172] = 16841\n" +
            "mem[40439] = 461272566\n" +
            "mem[60909] = 478018315\n" +
            "mem[43219] = 2154608\n" +
            "mem[25369] = 46117\n" +
            "mem[54852] = 79656\n" +
            "mask = XX10111001X100X1X1100X1X0001110001X1\n" +
            "mem[4213] = 900609324\n" +
            "mem[19327] = 28071\n" +
            "mem[30421] = 782\n" +
            "mem[4804] = 17293\n" +
            "mask = 0100X1000101X0010X010101X01000011001\n" +
            "mem[18139] = 1546181\n" +
            "mem[14021] = 33793814\n" +
            "mem[46699] = 2014\n" +
            "mem[51956] = 171606030\n" +
            "mem[29702] = 475302805\n" +
            "mem[18265] = 198549\n" +
            "mask = 0101X0110X1X0101X1X1X01101001X001XX1\n" +
            "mem[38962] = 132592128\n" +
            "mem[9436] = 7464578\n" +
            "mem[12650] = 49333\n" +
            "mem[8837] = 3234578\n" +
            "mask = 011X11101011X101111000XX001110001110\n" +
            "mem[61694] = 1206\n" +
            "mem[32263] = 20761769\n" +
            "mem[2116] = 193628\n" +
            "mem[13505] = 123039\n" +
            "mem[62164] = 14323289\n" +
            "mask = 1X101010100X0101X1110X01000101X00100\n" +
            "mem[21385] = 1022949\n" +
            "mem[51318] = 5667643\n" +
            "mem[17420] = 36980027\n" +
            "mem[29202] = 801\n" +
            "mask = 0101X01XX11X0101X10110X1010001001001\n" +
            "mem[15338] = 23103863\n" +
            "mem[10488] = 4521\n" +
            "mem[13172] = 17055515\n" +
            "mask = X10111X11X01X111X100000000111011X111\n" +
            "mem[36577] = 397263\n" +
            "mem[8992] = 11944917\n" +
            "mem[22064] = 738796\n" +
            "mem[17310] = 1562710\n" +
            "mem[30068] = 4950154\n" +
            "mask = 011111X10111X10X010X00X1X100X0010001\n" +
            "mem[31166] = 6551\n" +
            "mem[62218] = 1528\n" +
            "mem[11467] = 35999360\n" +
            "mem[39578] = 11530695\n" +
            "mem[30855] = 27864\n" +
            "mem[18369] = 1610323\n" +
            "mem[58953] = 12938251\n" +
            "mask = 01X111010X1111X0010X0XX010000X000111\n" +
            "mem[15411] = 1096\n" +
            "mem[49541] = 3181\n" +
            "mem[23568] = 276408\n" +
            "mem[45168] = 1721\n" +
            "mem[11394] = 155136\n" +
            "mask = 1111X1X011010001X101010X100XX11001X1\n" +
            "mem[61945] = 26647548\n" +
            "mem[63262] = 110741\n" +
            "mem[33783] = 158\n" +
            "mem[12753] = 200460\n" +
            "mem[43229] = 7579\n" +
            "mem[37084] = 26507\n" +
            "mask = 0100110011X10101010X00X01X11X1X10101\n" +
            "mem[65089] = 636807464\n" +
            "mem[5775] = 4440830\n" +
            "mem[52107] = 69328099\n" +
            "mem[38420] = 859060126\n" +
            "mem[21272] = 1700\n" +
            "mem[12062] = 176162\n" +
            "mem[12094] = 8733\n" +
            "mask = X100010111XX01X1X1010100X01101001X1X\n" +
            "mem[44718] = 33650499\n" +
            "mem[26507] = 165784650\n" +
            "mem[12622] = 2023\n" +
            "mem[5651] = 120398699\n" +
            "mask = 110001011X010X0111X10X00011110001X00\n" +
            "mem[44975] = 666498\n" +
            "mem[11614] = 751\n" +
            "mem[61354] = 5063\n" +
            "mem[4396] = 1131\n" +
            "mem[25418] = 882\n" +
            "mem[49245] = 64151\n" +
            "mask = 011X1X110111X1XX11X1100X01000X101001\n" +
            "mem[59013] = 1141214\n" +
            "mem[18016] = 95668408\n" +
            "mem[30067] = 18132964\n" +
            "mem[38900] = 286972459\n" +
            "mem[42265] = 13529062\n" +
            "mem[59369] = 6028326\n" +
            "mask = 0110110X010100010XXX0X00X0011X101X1X\n" +
            "mem[6479] = 8816055\n" +
            "mem[28451] = 29446\n" +
            "mem[61417] = 59156\n" +
            "mem[6694] = 15597\n" +
            "mem[29264] = 115437\n" +
            "mask = 11110100110101011101XX0001X1X1110101\n" +
            "mem[46886] = 114630\n" +
            "mem[17383] = 452299\n" +
            "mask = 010X110X11X10101000X001X011010000100\n" +
            "mem[19215] = 487176198\n" +
            "mem[59629] = 2120284\n" +
            "mem[27009] = 3064\n" +
            "mem[42335] = 22072\n" +
            "mem[514] = 2010\n" +
            "mask = 0100X1001101010X010000X0001000X00100\n" +
            "mem[37232] = 2564\n" +
            "mem[20561] = 29506163\n" +
            "mem[27396] = 380700410\n" +
            "mem[34075] = 868\n" +
            "mem[24967] = 1882926\n" +
            "mask = 010X1X01XX110111111X0XX0000X010X0101\n" +
            "mem[61084] = 3068852\n" +
            "mem[33028] = 188720342\n" +
            "mem[17375] = 62850\n" +
            "mask = X10X1X0X110101010X01001001101000X000\n" +
            "mem[24149] = 1815\n" +
            "mem[51489] = 197928369\n" +
            "mem[27694] = 231814\n" +
            "mem[11813] = 1002177793\n" +
            "mem[526] = 104755102\n" +
            "mem[22216] = 8396\n" +
            "mask = 0110111101010001X1X0X11100X010001111\n" +
            "mem[21083] = 2509191\n" +
            "mem[13215] = 172339241\n" +
            "mem[12386] = 106305632\n" +
            "mask = X1X101X11101010101000X010X11101101X1\n" +
            "mem[35709] = 64980388\n" +
            "mem[51838] = 62510\n" +
            "mem[48641] = 1174272\n" +
            "mem[42157] = 149\n" +
            "mask = 0X101100010100010X0X00X1100101111111\n" +
            "mem[35807] = 1100541\n" +
            "mem[10044] = 69616152\n" +
            "mem[3047] = 142725213\n" +
            "mask = 11101X1X10X101010111X001XX10X0X00100\n" +
            "mem[38049] = 110\n" +
            "mem[43097] = 14955394\n" +
            "mem[61810] = 3545867\n" +
            "mem[61238] = 5370\n" +
            "mem[20585] = 191903\n" +
            "mem[26133] = 24248\n" +
            "mask = 010X110X011X00010110010100X0XXX1X011\n" +
            "mem[15950] = 140910\n" +
            "mem[12062] = 424527462\n" +
            "mem[11876] = 236\n" +
            "mem[5182] = 4776\n" +
            "mem[50278] = 490\n" +
            "mask = 010XX011011101X1110101110111110110X0\n" +
            "mem[53736] = 2314\n" +
            "mem[12633] = 5053\n" +
            "mem[66] = 49557761\n" +
            "mask = 01X01101X10101010101000X00111110100X\n" +
            "mem[18849] = 911\n" +
            "mem[20666] = 12891678\n" +
            "mem[5609] = 10432\n" +
            "mem[59720] = 22145720\n" +
            "mem[17508] = 42631\n" +
            "mem[8585] = 3448\n" +
            "mask = 11X1X10X110101X101010X01X0011011X001\n" +
            "mem[30601] = 9140827\n" +
            "mem[30361] = 4166366\n" +
            "mem[46057] = 16057\n" +
            "mem[26983] = 251682577\n" +
            "mem[63197] = 1603252\n" +
            "mem[52893] = 462048575\n" +
            "mask = 011011100111X00X11100X00100010001XXX\n" +
            "mem[17534] = 25807901\n" +
            "mem[4932] = 106350673\n" +
            "mem[42192] = 735653575\n" +
            "mem[10874] = 59007\n" +
            "mask = 01000101110001X1XX001000001X1X011111\n" +
            "mem[48049] = 386\n" +
            "mem[1538] = 138451275\n" +
            "mem[50333] = 15707\n" +
            "mask = 0101100X01110111XX1X0000001011001X01\n" +
            "mem[18139] = 102960\n" +
            "mem[41277] = 5837\n" +
            "mem[44484] = 29937\n" +
            "mask = 11101XX10X1X0001011000100X010010X00X\n" +
            "mem[30615] = 95201946\n" +
            "mem[719] = 3697022\n" +
            "mem[27391] = 150969140\n" +
            "mem[62680] = 427952\n" +
            "mem[7349] = 46922\n" +
            "mem[17375] = 41348888\n" +
            "mem[57800] = 1901\n" +
            "mask = 0010X11X1X11X011111X0011001X01001111\n" +
            "mem[46994] = 118757653\n" +
            "mem[32947] = 23571\n" +
            "mem[8653] = 1364\n" +
            "mem[3767] = 6954112\n" +
            "mask = 01001X0011010X000111X0X0X1X011011XXX\n" +
            "mem[37908] = 88438829\n" +
            "mem[20630] = 618075182\n" +
            "mem[21520] = 101250753\n" +
            "mem[10703] = 475904\n" +
            "mask = 001001101XX10010111X1X10100111XX11X1\n" +
            "mem[17310] = 2889476\n" +
            "mem[2725] = 463419\n" +
            "mem[65001] = 910330085\n" +
            "mask = X1011011X111010101X110X1X1X0010110X0\n" +
            "mem[15999] = 18586203\n" +
            "mem[12825] = 51333145\n" +
            "mem[29966] = 596120517\n" +
            "mem[26866] = 141039\n" +
            "mem[24223] = 415414\n" +
            "mem[24403] = 16110598\n" +
            "mask = 0XX00X01111X01110X011010101X01101010\n" +
            "mem[43382] = 150995\n" +
            "mem[28011] = 1021785\n" +
            "mem[60339] = 7805893\n" +
            "mem[37197] = 268431\n" +
            "mem[17792] = 253366088\n" +
            "mem[21437] = 24057926\n" +
            "mask = XX1011X111100X00111X1100010111010011\n" +
            "mem[18006] = 265940517\n" +
            "mem[55921] = 1634\n" +
            "mem[27656] = 17058\n" +
            "mem[4911] = 3686\n" +
            "mem[33243] = 8125794\n" +
            "mem[47537] = 146165365\n" +
            "mask = 110111011X01X1X10100X000010110111X0X\n" +
            "mem[5775] = 176470\n" +
            "mem[63017] = 24003454\n" +
            "mask = 00011X001X11XX11111X01101010X0000010\n" +
            "mem[1604] = 174349\n" +
            "mem[42888] = 7159712\n" +
            "mem[26615] = 1487\n" +
            "mask = 01X11101100111111X000X1001110001011X\n" +
            "mem[5344] = 8563500\n" +
            "mem[21234] = 166162105\n" +
            "mem[48935] = 10849963\n" +
            "mask = 01000101XX0001111000XX0000X010010X00\n" +
            "mem[24149] = 127627213\n" +
            "mem[27338] = 43164114\n" +
            "mem[47215] = 252815\n" +
            "mem[47431] = 32732410\n" +
            "mask = 01011100000100011X0X0XX101X111111100\n" +
            "mem[13412] = 4193068\n" +
            "mem[45046] = 148\n" +
            "mem[63535] = 11659\n" +
            "mem[6518] = 471308933\n" +
            "mask = 111X111011X1X1X101111X000100001101X1\n" +
            "mem[31114] = 118512878\n" +
            "mem[41334] = 1604\n" +
            "mem[7338] = 571\n" +
            "mem[6001] = 4126415\n" +
            "mem[5215] = 4392\n" +
            "mem[47836] = 1862\n" +
            "mem[22064] = 30804845\n" +
            "mask = 0111010X11X1010X0101X1110011101X0111\n" +
            "mem[13321] = 22426593\n" +
            "mem[37095] = 5357\n" +
            "mem[44281] = 467020\n" +
            "mem[62680] = 2721559\n" +
            "mask = 010001X0010100XX10010000011000101X00\n" +
            "mem[30615] = 261491\n" +
            "mem[31097] = 46202501\n" +
            "mem[27880] = 6002395\n" +
            "mem[51385] = 2780\n" +
            "mem[51435] = 43181943\n" +
            "mem[42192] = 107728750\n" +
            "mask = 101011100X1100110X1X00X0101X10X11101\n" +
            "mem[48366] = 859523\n" +
            "mem[14111] = 859\n" +
            "mem[21668] = 292390073\n" +
            "mem[8073] = 858\n" +
            "mem[12920] = 662378\n" +
            "mask = X111X10011X10101X10101XX01111011X101\n" +
            "mem[20630] = 4051571\n" +
            "mem[55963] = 367\n" +
            "mem[379] = 10962356\n" +
            "mem[33028] = 37\n" +
            "mem[24035] = 9459\n" +
            "mem[50949] = 2030\n" +
            "mask = 001011100X110X111X10101100011X010X11\n" +
            "mem[8437] = 47226\n" +
            "mem[41248] = 319\n" +
            "mem[9624] = 3503\n" +
            "mem[6875] = 5282\n" +
            "mask = 11X111XX100101010100X01XX1X1001X1000\n" +
            "mem[21292] = 1673693\n" +
            "mem[51132] = 10346473\n" +
            "mem[7504] = 4325\n" +
            "mask = 111X010X1101011101X100010XX1X0111100\n" +
            "mem[35415] = 6296\n" +
            "mem[19215] = 1263591\n" +
            "mem[49977] = 379136185\n" +
            "mem[62950] = 28156510\n" +
            "mem[8265] = 28662942\n" +
            "mask = 01X110X01X1X0X11X1X00110000X01000100\n" +
            "mem[54672] = 131784041\n" +
            "mem[11394] = 24602\n" +
            "mem[24646] = 10584\n" +
            "mem[44349] = 4883\n" +
            "mem[54743] = 2940969\n" +
            "mem[8265] = 14841530\n" +
            "mask = 0X10111X11100X0011X011XX00X011011010\n" +
            "mem[49374] = 45910\n" +
            "mem[25923] = 368017518\n" +
            "mem[25114] = 8076340\n" +
            "mem[62690] = 904875563\n" +
            "mask = 010X11X0X1X1010X01010X01101001110101\n" +
            "mem[39408] = 3080\n" +
            "mem[6918] = 125955053\n" +
            "mem[27880] = 29186\n" +
            "mask = 01001101X101010X0101X1001111100010X1\n" +
            "mem[11813] = 153838914\n" +
            "mem[20585] = 1917\n" +
            "mem[21385] = 1881773\n" +
            "mem[8556] = 25758757\n" +
            "mem[22435] = 802061\n" +
            "mem[27631] = 13285866\n" +
            "mask = 01101110111X0001011X10100X0100X10101\n" +
            "mem[23441] = 186656612\n" +
            "mem[2186] = 189388742\n" +
            "mem[12866] = 874882\n" +
            "mem[12947] = 23895\n" +
            "mem[20630] = 77211\n" +
            "mem[42083] = 63015239\n" +
            "mem[51838] = 4984972\n" +
            "mask = 00101110X111001X1X10001XX0X111XXX101\n" +
            "mem[14789] = 244532376\n" +
            "mem[21292] = 736136092\n" +
            "mem[10874] = 513949\n" +
            "mem[16755] = 12361\n" +
            "mem[5416] = 22987\n" +
            "mem[39578] = 106587\n" +
            "mask = 010X11000X010001X1XXX0110011X1X1110X\n" +
            "mem[43479] = 61\n" +
            "mem[47199] = 15617564\n" +
            "mem[18265] = 6027808\n";

    @Test
    public void test() {

    }
}