package done;

import org.junit.Test;
import util.BatchSupplier;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("unused")
public class TicketTranslation {

    static final String PREFIX = "departure";
//    static final String PREFIX = "class";

    static List<String[]> batches;

    static final Map<String, FieldSpec> specs = new HashMap<>();
    static Ticket myTicket;
    static Ticket[] tickets;

    static String[] fields;
    static List<List<String>> candidates;

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT2)) {
            batches = Stream.generate(new BatchSupplier(in).withDelimiter(';'))
                    .takeWhile(Objects::nonNull)
                    .map(batch -> batch.split(";"))
                    .collect(toList());

            parseSpecs(batches.get(0));
            parseMyTicket(batches.get(1));
            parseTickets(batches.get(2));

            fields = new String[specs.size()];
            candidates = new ArrayList<>(specs.size());
            for (int i = 0; i < specs.size(); i++) {
                candidates.add(new ArrayList<>(specs.keySet()));
            }

            Stream.of(tickets).forEach(ticket -> {
                for (long num : ticket.numbers) {
                    if (specs.values().stream().noneMatch(spec -> spec.matches(num))) {
                        ticket.invalidNumber = num;
                        break;
                    }
                }
            });

            tickets = Stream.of(tickets)
                    .filter(t -> t.invalidNumber == -1)
                    .toArray(Ticket[]::new);

            // zjistit ktere pole je ktere
            checkTicket(myTicket);
            Stream.of(tickets).forEach(TicketTranslation::checkTicket);

            int counter = 0;
            while (Stream.of(fields).anyMatch(Objects::isNull)) {
                for (int i = 0; i < candidates.size(); i++) {
                    List<String> candidate = candidates.get(i);
                    if (candidate.size() == 1) {
                        addField(i, candidate);
                    }
                }
                if (counter++ > 20) break;
            }

            System.out.println("=========================");
            candidates.forEach(System.out::println);
            System.out.println("=========================");
            Stream.of(fields).forEach(System.out::println);
            System.out.println("=========================");

            // vypocitat produkt
            long result = LongStream.of(myTicket.numbers)
                    .mapToObj(NumberWithIndex::of)
                    .filter(NumberWithIndex::fieldHasPrefix)
                    .mapToLong(NumberWithIndex::number)
                    .reduce(1L, (x, y) -> x * y);

            System.out.println("Result: " + result);
        }
    }

    private static void checkTicket(Ticket ticket) {
        for (int i = 0; i < ticket.numbers.length; i++) {
            long num = ticket.numbers[i];
            final List<String> candidate = candidates.get(i);
            specs.values().stream()
                    .filter(spec -> !spec.matches(num))
                    .forEach(spec -> candidate.remove(spec.name));
            if (candidate.size() == 1) {
                addField(i, candidate);
            }
        }
    }

    private static void addField(int index, List<String> candidate) {
        final String field = candidate.get(0);
        candidates.stream()
                .filter(c -> c != candidate)
                .forEach(c -> c.remove(field));
        fields[index] = field;
    }

    private static void parseMyTicket(String[] batch) {
        myTicket = Ticket.from(batch[2]);
    }

    private static void parseTickets(String[] batch) {
        tickets = Stream.of(batch)
                .skip(2)
                .map(Ticket::from)
                .toArray(Ticket[]::new);
    }

    private static void parseSpecs(String[] batch) {
        Stream.of(batch).filter(not(String::isBlank)).forEach(line -> {
            String[] field = line.trim().split(":");
            specs.put(field[0], FieldSpec.parse(field[0].trim(), field[1].trim()));
        });
    }

    static class Ticket {
        final long[] numbers;
        long invalidNumber = -1;

        Ticket(long[] numbers) {
            this.numbers = numbers;
        }

        static Ticket from(String line) {
            long[] numbers = Stream.of(line.split(","))
                    .mapToLong(Long::parseLong)
                    .toArray();
            return new Ticket(numbers);
        }
    }

    private static final String INPUT = """
            class: 0-1 or 4-19
            row: 0-5 or 8-19
            seat: 0-13 or 16-19
                       
            your ticket:
            11,12,13
                       
            nearby tickets:
            3,9,18
            15,1,5
            5,14,9
            """;

    private static final String INPUT2 = """
            departure location: 29-766 or 786-950
            departure station: 40-480 or 491-949
            departure platform: 46-373 or 397-957
            departure track: 33-657 or 673-970
            departure date: 31-433 or 445-961
            departure time: 33-231 or 250-966
            arrival location: 48-533 or 556-974
            arrival station: 42-597 or 620-957
            arrival platform: 32-119 or 140-967
            arrival track: 28-750 or 762-973
            class: 26-88 or 101-950
            duration: 30-271 or 293-974
            price: 33-712 or 718-966
            route: 49-153 or 159-953
            row: 36-842 or 851-972
            seat: 43-181 or 194-955
            train: 29-500 or 513-964
            type: 32-59 or 73-974
            wagon: 47-809 or 816-957
            zone: 44-451 or 464-955
                        
            your ticket:
            151,103,173,199,211,107,167,59,113,179,53,197,83,163,101,149,109,79,181,73
                        
            nearby tickets:
            339,870,872,222,255,276,706,890,583,718,924,118,201,141,59,581,931,143,221,919
            400,418,807,726,84,142,820,112,228,687,335,855,761,740,627,532,369,313,147,620
            474,926,741,424,632,982,876,926,738,936,300,726,400,321,348,160,421,464,78,258
            337,683,885,368,214,215,620,650,321,311,200,696,838,880,526,812,821,891,829,204
            678,69,480,939,219,736,628,948,178,296,204,890,199,526,736,210,170,923,228,726
            574,55,871,945,347,207,840,707,442,791,159,216,427,312,947,326,738,801,264,54
            811,259,417,932,870,634,945,408,706,766,303,402,942,50,415,150,839,674,904,836
            670,358,701,172,231,914,875,205,586,517,576,206,796,400,363,303,295,932,73,308
            319,949,312,803,217,6,719,870,202,883,111,532,467,801,83,202,331,637,930,412
            580,262,196,529,401,806,446,104,326,657,589,789,876,817,470,653,167,191,634,678
            720,217,118,422,190,115,684,827,317,228,524,569,531,471,322,408,896,566,875,302
            678,683,218,820,896,303,296,316,229,758,114,261,825,750,901,428,479,556,373,404
            502,259,577,77,260,588,340,199,626,575,171,422,372,83,111,902,300,863,800,886
            577,216,195,804,298,948,83,807,878,656,740,743,854,740,219,331,714,176,683,199
            492,187,259,528,853,80,808,887,149,723,324,922,170,323,884,825,710,562,416,471
            702,55,141,261,743,256,632,693,787,581,363,851,578,74,164,146,413,224,829,282
            370,161,470,207,876,57,564,103,195,216,468,632,161,734,487,150,570,251,311,196
            242,430,115,575,806,258,109,85,433,794,85,258,647,596,84,339,215,879,203,74
            912,449,174,371,857,556,743,537,521,643,903,745,764,207,295,896,431,901,592,332
            687,361,855,683,298,828,421,894,163,214,308,110,643,495,106,198,613,205,322,582
            318,578,83,318,150,182,298,740,424,306,411,895,50,265,887,465,912,262,294,428
            320,948,74,114,643,801,928,280,868,930,822,140,620,696,627,520,692,820,526,764
            622,477,314,253,419,73,333,924,750,432,705,894,114,827,364,946,577,269,789,463
            639,742,840,541,656,787,222,637,728,734,558,744,173,801,80,629,634,572,151,326
            830,468,597,75,259,762,331,315,151,369,308,419,447,207,523,268,118,719,501,839
            496,312,525,343,559,77,114,404,362,595,656,301,622,337,741,818,816,352,1,449
            432,512,627,739,729,627,218,829,595,471,641,447,639,350,220,718,573,319,859,363
            646,368,258,320,916,520,328,327,180,357,646,620,530,208,269,84,264,53,184,163
            904,657,329,897,327,339,674,803,110,327,308,78,883,353,150,446,296,645,539,932
            472,726,685,676,406,419,594,419,699,364,416,868,106,53,662,856,859,571,674,571
            301,741,900,869,259,727,557,975,334,913,496,948,929,79,165,680,838,465,591,902
            939,201,586,897,948,725,625,224,923,801,910,723,361,338,55,217,721,300,674,777
            362,211,592,573,711,831,170,949,171,408,647,578,228,349,629,780,582,567,84,349
            317,874,623,704,934,526,898,797,447,485,400,589,679,109,827,107,353,807,859,513
            519,108,304,162,888,88,742,637,789,923,749,404,171,175,708,432,559,200,557,444
            787,589,518,397,762,200,734,222,468,252,306,560,558,701,905,656,155,689,51,258
            354,159,938,734,932,636,478,162,562,718,315,712,933,862,530,877,118,535,366,797
            189,338,517,302,414,401,167,905,947,500,895,940,405,222,864,706,835,352,269,825
            757,404,212,426,628,113,945,480,398,271,353,364,225,804,643,84,416,728,936,223
            337,529,631,589,909,314,174,820,257,199,822,359,344,445,929,20,513,450,212,928
            254,927,865,202,851,733,897,329,433,224,319,674,857,722,837,207,419,84,728,375
            416,464,822,368,920,175,0,213,799,200,648,529,398,889,678,407,180,870,496,786
            712,335,80,824,826,416,261,400,904,630,255,691,259,723,344,354,786,857,774,195
            945,257,57,260,642,817,934,909,574,358,269,683,764,176,365,427,549,878,500,791
            807,140,308,885,866,623,80,111,648,848,572,643,853,854,560,934,699,52,580,164
            895,590,647,730,540,874,255,115,304,117,224,399,168,819,420,525,306,160,580,595
            594,265,401,920,855,62,352,706,908,835,925,908,829,620,476,51,251,73,875,696
            261,748,411,706,409,53,470,202,227,918,653,398,516,518,416,187,917,836,649,222
            288,690,877,468,620,451,147,919,251,408,836,584,319,729,640,445,673,87,650,856
            308,806,477,875,329,928,948,800,417,574,220,991,355,51,693,838,747,794,498,913
            706,582,499,834,887,109,677,857,280,407,651,517,468,407,403,303,515,881,656,523
            256,597,344,577,636,702,561,738,683,884,268,858,73,201,421,721,646,739,424,992
            903,211,920,623,160,152,567,786,852,423,179,258,690,493,110,505,823,303,721,373
            397,893,747,819,729,88,651,893,699,229,744,948,712,104,741,643,511,854,876,706
            929,880,820,532,259,621,307,480,900,59,915,188,863,624,119,252,928,935,451,885
            335,893,475,733,787,850,629,839,530,231,451,942,260,718,654,473,255,495,406,464
            451,822,629,745,226,595,295,862,408,592,498,474,77,110,845,354,906,424,913,936
            75,891,446,347,468,521,312,320,346,579,900,205,528,307,370,890,8,269,826,566
            566,353,58,360,629,793,649,567,259,736,261,531,118,880,244,145,581,172,885,471
            107,371,947,936,250,468,624,888,334,622,947,327,837,941,360,521,754,199,688,905
            313,163,370,367,8,174,341,792,153,736,725,581,313,203,872,225,590,720,841,475
            210,823,329,556,763,691,371,202,807,902,741,413,303,832,651,802,473,358,990,897
            718,652,891,367,702,640,645,883,817,231,590,842,350,820,291,681,422,859,691,338
            349,408,890,915,116,719,872,589,586,620,116,206,683,353,317,81,319,86,849,797
            84,520,856,641,254,206,209,356,178,476,728,114,559,802,579,377,298,112,941,853
            518,741,500,466,796,676,833,235,330,854,494,762,58,923,908,228,335,433,85,565
            418,697,943,513,854,907,824,872,479,81,372,324,688,216,299,407,621,492,63,685
            559,652,645,107,685,577,870,370,509,58,371,85,445,800,174,255,412,475,640,837
            85,366,789,597,625,701,275,166,632,82,854,422,628,863,855,213,448,213,517,263
            447,373,853,469,912,545,712,695,749,673,299,647,744,826,893,367,865,448,116,943
            570,520,859,919,742,221,577,316,946,358,82,910,892,595,584,559,644,593,665,879
            645,345,399,430,697,86,641,642,239,764,325,253,728,82,891,372,402,270,400,153
            144,533,735,296,806,648,521,747,115,194,405,764,188,861,838,584,872,373,738,744
            446,180,652,85,429,330,172,517,629,566,167,922,177,884,751,868,919,102,494,930
            306,946,257,637,886,231,836,566,108,210,357,720,791,819,826,489,346,159,420,883
            360,622,764,398,398,82,323,165,403,185,557,447,635,171,493,211,736,570,766,79
            840,174,102,911,424,821,878,311,498,785,798,572,211,268,59,177,732,853,475,165
            109,228,693,210,300,882,184,419,872,939,706,693,596,635,625,905,566,351,202,727
            686,414,806,638,869,737,407,506,912,465,448,738,913,118,514,321,363,180,905,345
            928,348,305,110,916,827,222,681,197,313,827,903,148,761,498,523,643,297,430,897
            317,816,623,799,152,518,491,801,934,762,694,708,583,257,818,400,931,358,899,755
            418,943,493,888,559,252,826,222,680,726,426,639,104,579,886,732,844,111,856,817
            478,728,865,930,591,340,734,401,900,399,116,839,78,106,315,111,667,631,302,478
            744,691,703,984,901,301,75,432,687,336,630,421,527,116,944,111,930,641,497,729
            529,930,589,712,588,59,817,76,566,704,319,253,935,423,692,753,626,217,299,142
            911,403,257,196,179,178,55,627,468,546,684,327,255,923,912,252,646,266,206,207
            889,638,475,473,866,227,789,148,72,57,50,366,269,743,878,901,627,227,586,199
            833,976,570,51,335,212,173,110,652,424,446,858,677,73,787,107,877,159,587,165
            333,831,361,52,165,59,145,528,935,57,262,366,884,198,354,223,985,808,170,521
            928,301,762,573,572,832,728,708,933,150,689,308,680,579,603,313,354,582,368,477
            437,686,400,937,162,594,878,414,170,209,259,886,432,208,348,228,399,519,708,574
            85,633,54,353,886,917,161,499,496,78,804,119,652,496,511,919,476,306,728,51
            451,718,399,578,639,651,293,940,364,75,450,364,799,341,851,295,465,335,754,266
            371,268,852,750,706,735,113,752,863,795,467,572,794,497,838,310,160,367,251,471
            883,269,624,268,268,557,68,197,590,330,678,316,429,266,877,414,896,733,899,746
            991,521,595,300,422,471,686,620,159,341,448,730,694,513,342,575,743,213,790,684
            178,252,643,595,697,59,703,328,424,76,567,173,254,880,805,728,519,798,332,157
            297,373,312,687,566,491,571,109,448,581,720,873,552,695,806,446,351,854,594,150
            260,216,58,789,676,180,708,785,115,252,331,344,498,334,570,935,200,105,208,687
            207,484,171,323,466,722,794,833,323,50,763,347,729,252,710,176,910,328,74,649
            938,293,838,840,341,429,702,483,259,852,900,112,143,180,881,526,519,58,466,750
            700,172,253,365,871,563,643,520,945,587,253,852,492,731,493,281,310,112,573,220
            425,941,103,586,871,801,807,918,843,898,522,251,799,177,763,144,195,228,408,84
            149,256,792,82,711,419,533,946,835,271,76,681,172,141,120,451,178,633,358,297
            595,79,203,762,856,988,556,562,791,528,635,678,865,205,449,870,823,341,260,817
            407,24,904,411,818,365,859,654,491,817,297,86,674,218,360,840,620,54,806,708
            466,210,258,641,199,766,76,256,651,267,824,343,940,405,198,701,431,259,205,536
            561,164,326,563,765,897,177,805,207,571,425,572,349,734,907,233,255,310,54,88
            934,866,361,699,709,861,140,680,217,699,172,647,572,923,56,319,164,475,136,255
            689,587,52,885,751,517,624,915,790,347,172,366,162,941,893,916,358,419,421,226
            562,516,58,626,940,802,840,212,473,230,887,875,180,893,332,942,274,896,718,170
            863,557,196,692,263,53,145,340,211,61,117,816,417,108,171,111,683,312,559,75
            250,808,732,474,174,261,312,587,349,167,573,934,790,334,819,643,784,212,564,500
            480,103,319,230,477,428,119,563,896,421,568,712,923,519,737,672,882,398,496,625
            424,787,54,256,917,880,415,466,309,410,59,836,230,189,594,927,620,730,561,168
            584,104,415,451,747,808,349,742,54,410,315,711,531,277,705,521,368,259,144,708
            365,467,337,75,993,147,223,519,150,206,864,946,763,51,596,105,736,310,695,146
            79,365,85,309,649,676,324,597,938,310,733,942,223,693,316,904,413,229,833,484
            946,921,52,450,229,180,700,268,16,701,919,885,730,682,494,836,786,852,171,220
            295,750,718,149,626,497,335,819,518,624,199,705,631,825,418,897,468,19,698,866
            556,584,807,269,721,213,420,721,870,812,88,359,180,829,738,789,295,877,151,360
            940,333,838,622,637,173,325,923,547,866,304,398,943,789,822,916,639,679,261,340
            480,266,711,197,322,112,362,169,705,828,84,862,719,17,593,864,142,561,916,356
            337,675,859,872,586,689,329,949,771,573,267,932,200,526,333,161,350,115,300,697
            562,362,942,150,924,215,330,162,753,565,498,321,307,709,641,697,863,578,205,868
            655,464,886,153,318,402,818,733,490,73,354,853,936,858,637,473,203,301,177,312
            705,429,402,348,882,471,644,193,427,687,804,841,414,270,332,720,228,633,787,880
            265,927,325,889,345,403,88,168,219,696,230,570,733,521,833,217,482,711,522,742
            335,702,271,178,592,569,845,913,176,649,299,916,788,253,205,860,146,113,710,168
            264,219,87,368,798,852,470,224,646,367,355,710,655,17,649,219,942,169,314,118
            54,710,880,343,270,468,269,850,167,50,675,303,688,750,419,870,324,674,728,914
            528,423,153,101,939,471,418,21,414,926,656,446,211,826,681,863,897,530,839,347
            909,298,338,890,335,216,693,171,584,480,228,987,198,252,582,657,641,218,824,791
            254,578,837,816,936,271,368,268,167,101,685,516,743,399,465,542,495,626,888,319
            268,140,857,844,79,763,115,322,735,159,409,519,857,699,317,917,520,910,75,728
            628,2,410,568,299,252,168,683,587,303,743,141,702,723,498,911,766,165,465,800
            431,583,212,933,253,358,981,114,766,764,942,361,524,586,203,730,562,627,449,926
            631,337,858,720,866,567,723,828,731,926,145,204,640,653,231,724,688,371,482,144
            83,571,836,361,797,349,834,809,704,293,839,301,271,219,797,743,803,637,719,811
            844,59,356,851,692,180,786,476,703,801,323,797,943,86,432,329,943,596,868,745
            230,922,302,180,118,241,231,867,469,639,161,207,807,325,825,74,338,526,722,855
            628,0,416,116,143,467,262,937,269,590,80,926,176,523,519,674,523,911,651,366
            873,934,81,406,163,945,905,216,359,795,819,554,314,583,722,568,597,317,204,880
            433,683,181,426,583,631,826,873,894,938,588,854,308,267,885,222,861,843,719,144
            905,825,294,143,203,465,794,674,346,364,180,88,865,168,905,947,897,943,252,717
            492,416,629,719,827,108,921,163,745,930,500,343,426,740,777,326,574,149,250,589
            940,340,584,722,251,520,880,532,151,398,795,886,869,253,185,304,468,311,852,877
            833,371,112,810,56,516,829,352,641,466,295,528,476,718,413,725,559,917,818,533
            677,355,203,373,356,398,573,831,750,410,697,809,53,597,435,946,173,890,905,152
            588,465,475,201,523,521,340,897,427,834,875,718,635,407,733,323,908,341,440,595
            478,496,748,82,890,210,623,809,315,273,58,681,655,101,640,325,621,337,494,299
            216,761,834,342,766,808,466,325,51,480,221,721,433,411,403,342,267,620,218,255
            301,58,310,355,891,149,474,681,76,677,518,734,532,745,908,333,519,418,278,446
            842,527,339,354,940,828,198,478,413,181,695,84,637,988,712,938,165,147,696,211
            795,712,522,698,402,310,813,465,910,630,263,331,731,706,936,686,226,829,199,828
            575,786,691,828,447,512,730,270,56,722,794,108,175,346,639,174,217,347,652,822
            530,732,51,688,74,696,416,309,264,521,209,727,710,259,361,401,559,909,260,510
            368,590,119,906,200,725,329,696,942,140,118,933,474,847,657,557,652,823,162,332
            107,940,194,558,335,798,926,366,528,918,880,867,695,491,268,226,70,765,150,732
            161,684,179,184,732,749,265,529,349,87,934,467,723,111,216,201,861,795,449,801
            649,141,914,636,468,196,656,703,813,897,852,472,347,56,337,809,736,901,707,747
            594,141,425,316,174,573,885,357,873,595,726,334,59,745,717,887,207,200,310,357
            732,368,862,346,795,657,531,464,721,356,732,422,114,735,328,421,741,332,394,164
            264,261,806,898,471,170,763,819,642,690,500,267,847,592,73,906,171,175,909,532
            77,852,267,578,219,913,795,839,85,920,645,560,303,871,404,804,595,206,727,138
            264,323,50,338,301,417,702,817,791,666,172,674,597,721,876,705,576,449,786,945
            880,633,356,366,197,692,209,426,261,940,643,634,926,160,269,590,191,293,928,529
            295,700,113,627,643,557,141,820,379,140,430,677,791,450,686,118,322,479,495,423
            467,749,178,367,520,566,938,114,176,226,894,470,908,824,806,373,436,596,497,575
            816,233,748,518,829,424,901,643,56,565,333,570,561,948,114,430,632,901,301,708
            208,268,409,297,571,838,722,62,889,885,256,725,166,869,168,150,647,406,498,762
            646,695,359,76,710,363,748,887,416,515,696,227,166,86,742,308,140,721,311,663
            635,479,734,830,338,224,563,359,88,587,676,370,582,871,840,476,247,790,856,584
            226,101,270,301,423,103,264,674,724,361,349,620,646,337,801,830,445,688,746,64
            821,112,882,887,295,764,760,789,623,198,655,922,109,809,922,88,399,419,925,305
            891,308,685,214,106,147,228,903,114,497,309,743,175,399,416,416,354,935,747,990
            642,449,852,913,621,752,318,255,331,478,229,680,630,871,865,922,253,422,151,201
            115,732,583,361,316,500,210,77,319,737,448,625,371,651,202,949,403,228,263,999
            711,280,169,354,364,255,176,633,560,225,194,214,252,229,168,117,329,348,344,681
            168,471,181,476,368,691,918,19,910,325,629,793,298,340,880,570,201,686,795,398
            763,206,648,686,679,163,250,167,178,917,301,205,69,372,175,201,802,629,258,513
            340,304,195,422,220,450,942,500,620,505,88,924,266,566,832,50,647,480,222,840
            643,560,923,526,880,942,411,945,184,527,630,725,258,118,908,868,205,422,416,523
            376,639,418,938,425,905,650,891,317,269,495,940,113,582,728,178,176,589,802,78
            775,312,631,797,931,103,109,588,109,464,680,293,655,149,167,821,415,816,260,881
            478,655,496,170,419,806,582,115,908,639,110,153,644,571,543,164,103,210,175,228
            206,211,889,225,703,901,857,530,648,144,81,934,110,923,305,846,433,745,529,646
            493,763,224,310,921,644,355,479,81,115,269,265,886,9,110,625,694,675,350,636
            836,518,345,917,78,445,478,892,945,476,417,107,862,744,380,327,872,935,884,691
            928,475,349,927,404,801,203,924,667,915,143,932,119,197,864,513,646,802,359,592
            194,369,582,115,422,531,675,373,166,640,311,268,703,710,826,699,722,114,795,847
            179,257,525,695,324,197,745,719,171,450,707,576,814,478,888,426,828,749,268,916
            654,520,898,640,929,586,425,828,862,861,861,266,743,297,0,689,884,116,414,366
            318,365,189,651,586,294,326,429,735,569,934,109,447,914,675,701,701,803,106,634
            145,229,170,364,581,722,918,875,866,410,566,982,635,321,750,164,741,335,403,918
            890,497,731,81,432,417,919,338,355,297,153,640,675,799,620,684,518,4,209,301
            197,733,109,834,889,207,898,400,140,525,424,583,416,448,902,201,902,688,472,14
            0,809,569,206,625,431,56,448,913,800,476,344,882,269,166,314,908,202,373,570
            206,201,414,688,166,718,677,696,978,431,594,263,628,449,868,360,807,305,346,499
            217,513,429,924,143,167,526,348,268,101,260,654,917,866,812,748,59,816,589,655
            788,52,203,270,361,796,895,466,596,149,864,929,895,325,351,702,883,316,892,239
            831,500,911,524,820,498,547,315,737,103,494,406,220,358,467,414,725,415,466,472
            577,821,171,9,863,807,398,354,266,268,339,724,932,691,334,679,450,264,228,654
            116,497,360,294,166,469,919,870,321,592,566,206,883,214,736,309,806,762,810,748
            839,525,166,295,269,203,892,578,740,645,305,583,874,634,681,499,998,912,365,638
            419,692,496,597,297,945,929,410,911,699,733,868,831,709,932,680,890,199,728,615
            944,820,324,693,736,874,637,872,528,249,681,149,339,151,464,591,225,314,315,313
            270,639,345,944,690,150,201,822,851,468,876,830,511,303,57,923,762,869,472,316
            641,793,104,944,23,870,307,788,877,591,860,328,492,899,472,328,419,361,903,820
            799,992,500,116,516,142,903,451,177,477,931,560,744,77,570,52,344,695,816,50
            691,227,707,202,302,813,149,623,261,116,575,564,704,824,653,890,937,101,174,256
            257,112,339,420,261,834,492,907,912,649,874,838,729,826,271,345,361,470,637,193
            339,812,319,906,401,575,867,586,207,351,697,497,855,740,407,347,945,736,152,896
            925,845,88,477,58,216,885,565,623,259,142,307,621,304,115,476,939,700,202,469
            808,786,83,651,174,623,579,224,224,789,208,161,7,722,116,350,698,733,883,199
            682,421,887,432,847,336,524,654,361,371,161,480,59,880,797,348,407,308,681,403
            686,571,408,941,788,705,480,365,678,728,631,591,152,555,119,725,595,936,143,142
            825,331,993,830,876,399,142,880,741,140,694,947,293,532,445,855,78,912,357,412
            898,315,491,897,927,807,165,596,695,563,877,700,56,307,480,253,139,77,117,762
            299,690,341,493,500,152,351,810,631,339,828,87,205,565,725,696,207,828,270,404
            484,260,809,639,52,620,910,223,499,706,465,348,85,698,116,622,579,707,826,571
            621,655,13,445,733,686,411,449,467,898,491,75,344,805,148,356,195,79,449,653
            988,891,824,632,229,909,571,176,928,586,708,117,471,560,864,805,725,161,114,934
            741,165,570,908,632,728,938,570,516,220,86,301,655,882,581,637,877,475,234,320
            67,801,766,731,254,827,916,572,449,621,628,596,478,175,166,696,323,533,416,701
            720,58,837,307,695,306,629,215,522,706,856,591,281,110,175,872,532,524,181,167
            525,841,651,174,940,530,165,869,423,250,920,705,724,742,112,144,378,259,467,360
            740,648,791,679,674,404,165,265,151,256,348,162,880,150,66,353,80,636,258,115
            886,575,150,754,365,562,362,522,498,571,639,364,834,169,55,562,470,446,466,366
            552,361,412,118,318,724,652,828,450,627,469,594,424,620,519,108,884,884,53,349
            519,587,585,341,892,56,175,316,312,472,874,496,267,358,946,874,812,218,943,228
            927,307,867,229,597,448,929,557,688,886,269,331,718,345,471,651,369,838,619,469
            203,796,199,515,424,212,572,529,220,786,170,889,198,359,596,65,626,569,933,687
            208,105,266,278,269,653,531,627,881,253,81,632,836,445,574,788,348,209,166,586
            425,543,702,474,424,673,918,747,628,762,175,931,832,863,79,213,109,557,881,494
            606,789,514,465,893,339,315,494,314,921,944,477,724,920,856,150,733,932,897,415
            """;

    @Test
    public void test() {

    }

    record FieldSpec(String name, List<Range> ranges) {
        static FieldSpec parse(String name, String ranges) {
            return new FieldSpec(
                    name,
                    Stream.of(ranges.split("or"))
                            .map(Range::of)
                            .collect(toList()));
        }

        boolean matches(long num) {
            return ranges().stream().anyMatch(range -> range.match(num));
        }

        record Range(int low, int high) {
            static Range of(String range) {
                String[] parts = range.trim().split("-");
                return new Range(parseInt(parts[0].trim()), parseInt(parts[1].trim()));
            }

            public boolean match(long num) {
                return num >= low && num <= high;
            }
        }
    }

    record NumberWithIndex(long number, int index) {
        boolean fieldHasPrefix() {
            return fields[index()].startsWith(PREFIX);
        }

        static NumberWithIndex of(long number) {
            return new NumberWithIndex(number, idx.getAsInt());
        }

        private static final IntSupplier idx = new IntSupplier() {
            int idx = 0;

            @Override
            public int getAsInt() {
                return idx++;
            }
        };
    }
}