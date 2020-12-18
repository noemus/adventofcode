package done;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Passports {

    static class Passport {
        final Map<Field, String> fields = new EnumMap<>(Field.class);

        Passport(String[] batch) {
            for (String entry : batch) {
                addField(entry);
            }
        }

        private void addField(String entry) {
            int colon = entry.indexOf(':');
            if (colon != -1) {
                Field field = Field.from(entry.substring(0, colon));
                if (field != null) {
                    String value = entry.substring(colon + 1);
                    if (!value.isEmpty()) {
                        fields.put(field, value);
                    }
                }
            }
        }

        boolean valid() {
            int size = fields.size();

            boolean hasRequiredFields = fields.containsKey(Field.cid)
                    ? size == FIELDS_COUNT
                    : size == (FIELDS_COUNT - 1);

            boolean allValid = fields.entrySet()
                    .stream()
                    .allMatch(entry -> entry.getKey().isValid(entry.getValue()));

            return allValid && hasRequiredFields;
        }

        @Override
        public String toString() {
            return fields.toString()
                    + " - COUNT = " +fields.size()
                    + " - CID = " + fields.getOrDefault(Field.cid, "N/A")
                    + " || " + (valid() ? "VALID" : "INVALID");
        }

        static final Field[] FIELDS = Field.values();
        static final int FIELDS_COUNT = FIELDS.length;

        /**
         byr (Birth Year) - four digits; at least 1920 and at most 2002.
         iyr (Issue Year) - four digits; at least 2010 and at most 2020.
         eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
         hgt (Height) - a number followed by either cm or in:
         If cm, the number must be at least 150 and at most 193.
         If in, the number must be at least 59 and at most 76.
         hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
         ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
         pid (Passport ID) - a nine-digit number, including leading zeroes.
         cid (Country ID) - ignored, missing or not.
         */
        enum Field {
            byr("Birth Year") {
                @Override
                boolean isValid(String value) {
                    return validIntRange(value, 1920, 2002);
                }
            },
            iyr("Issue Year") {
                @Override
                boolean isValid(String value) {
                    return validIntRange(value, 2010, 2020);
                }
            },
            eyr("Expiration Year") {
                @Override
                boolean isValid(String value) {
                    return validIntRange(value, 2020, 2030);
                }
            },
            hgt("Height") {
                @Override
                boolean isValid(String value) {
                    return validHeight(value, "cm", 150, 193) || validHeight(value, "in", 59, 76);
                }
            },
            hcl("Hair Color") {
                @Override
                boolean isValid(String value) {
                    return value.matches("[#][0-9a-f]{6}");
                }
            },
            ecl("Eye Color") {
                @Override
                boolean isValid(String value) {
                    return value.matches("amb|blu|brn|gry|grn|hzl|oth");
                }
            },
            pid("Passport ID") {
                @Override
                boolean isValid(String value) {
                    return value.matches("[0-9]{9}");
                }
            },
            cid("Country ID"),
            ;

            private final String text;

            Field(String text) {
                this.text = text;
            }

            boolean isValid(String value) {
                return true;
            }

            public static Field from(String text) {
                for (Field field : FIELDS) {
                    if (field.name().equals(text)) {
                        return field;
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return text;
            }
        }
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(INPUT)) {
            List<Passport> passports = Stream.generate(new BatchSupplier(in))
                    .takeWhile(Objects::nonNull)
                    .map(batch -> batch.split("[\\s]"))
                    .map(Passport::new)
                    .collect(toList());

            passports.forEach(System.out::println);

            long validPasswports = passports.stream().filter(Passport::valid).count();

            System.out.println("Result: " + validPasswports);
        }
    }

    private static final String INPUT2 = "" +
            "ecl:gry pid:860033327 eyr:2020 hcl:#fffffd\n" +
            "byr:1937 iyr:2017 cid:147 hgt:183cm\n" +
            "\n" +
            "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884\n" +
            "hcl:#cfa07d byr:1929\n" +
            "\n" +
            "hcl:#ae17e1 iyr:2013\n" +
            "eyr:2024\n" +
            "ecl:brn pid:760753108 byr:1931\n" +
            "hgt:179cm\n" +
            "\n" +
            "hcl:#cfa07d eyr:2025 pid:166559648\n" +
            "iyr:2011 ecl:brn hgt:59in\n";

    private static final String INPUT = "" +
            "hcl:5d90f0 cid:270 ecl:#66dc9c hgt:62cm byr:1945 pid:63201172 eyr:2026\n" +
            "\n" +
            "ecl:amb byr:1943 iyr:2014 eyr:2028\n" +
            "pid:333051831\n" +
            "\n" +
            "byr:1971\n" +
            "eyr:2021 iyr:2015 pid:158388040 hcl:#18171d ecl:brn hgt:179cm\n" +
            "\n" +
            "byr:1936\n" +
            "pid:707057570 iyr:2014 ecl:amb cid:299 eyr:2030\n" +
            "hcl:#c0946f hgt:186cm\n" +
            "\n" +
            "hgt:163cm iyr:2013 ecl:gry hcl:#86e981 byr:1939\n" +
            "eyr:2020 pid:241741372 cid:203\n" +
            "\n" +
            "ecl:brn hcl:#341e13\n" +
            "pid:686617364 byr:1929 eyr:2029 hgt:160cm cid:280 iyr:2020\n" +
            "\n" +
            "byr:2002 hcl:#623a2f\n" +
            "pid:253005469 iyr:2011 ecl:hzl hgt:184cm eyr:2027\n" +
            "\n" +
            "ecl:#bb984b eyr:2040\n" +
            "hgt:188in\n" +
            "iyr:2005 hcl:c5be8e pid:174cm cid:161 byr:2004\n" +
            "\n" +
            "ecl:oth iyr:2010 cid:128 hgt:153cm byr:1991\n" +
            "pid:24061445 eyr:2025 hcl:#54d43e\n" +
            "\n" +
            "hcl:z\n" +
            "iyr:2023 pid:981178503 ecl:gmt eyr:2038 byr:2004\n" +
            "\n" +
            "ecl:gry eyr:2022 iyr:1981 pid:566993828\n" +
            "byr:1941 hcl:#341e13 hgt:176cm\n" +
            "\n" +
            "eyr:2027 byr:1976\n" +
            "pid:350079989 ecl:blu iyr:2013 hgt:180cm hcl:#866857\n" +
            "\n" +
            "eyr:2029 hcl:#ceb3a1\n" +
            "ecl:lzr\n" +
            "iyr:2011 hgt:152cm byr:1986 pid:162999623\n" +
            "cid:240\n" +
            "\n" +
            "ecl:gry iyr:2017 hcl:#18171d byr:1926\n" +
            "eyr:2027 hgt:68in\n" +
            "cid:310 pid:560836007\n" +
            "\n" +
            "ecl:grn\n" +
            "cid:307\n" +
            "pid:#cdc803\n" +
            "byr:1975 eyr:2039 hgt:75cm\n" +
            "hcl:318b11 iyr:2022\n" +
            "\n" +
            "ecl:brn hgt:179cm eyr:2020 iyr:2016\n" +
            "pid:322103252 byr:1940 hcl:#b6652a\n" +
            "\n" +
            "hcl:#733820 hgt:188cm cid:70 eyr:2021 ecl:amb\n" +
            "byr:1996\n" +
            "iyr:2013 pid:412419084\n" +
            "\n" +
            "hgt:164cm iyr:2011 byr:1928 eyr:2020 hcl:#733820 pid:704914380 ecl:blu\n" +
            "\n" +
            "ecl:brn cid:267 eyr:2029 byr:2011\n" +
            "hcl:z pid:467662306 iyr:2026 hgt:104\n" +
            "\n" +
            "pid:224593036 eyr:2027\n" +
            "ecl:brn hcl:#341e13 iyr:2014\n" +
            "byr:1997\n" +
            "hgt:181cm\n" +
            "\n" +
            "eyr:2005 pid:9756449964\n" +
            "hcl:#fffffd byr:1999 ecl:dne hgt:152in iyr:2027\n" +
            "\n" +
            "byr:1998\n" +
            "iyr:2017 pid:618350852 hgt:156cm cid:193 ecl:amb\n" +
            "hcl:#602927 eyr:2029\n" +
            "\n" +
            "byr:2021 pid:3395281192\n" +
            "hcl:z hgt:167in ecl:grt eyr:2008 iyr:2025\n" +
            "\n" +
            "cid:206 pid:735212085 eyr:2020 byr:1950 hgt:153cm\n" +
            "ecl:blu iyr:2019\n" +
            "hcl:#733820\n" +
            "\n" +
            "eyr:2021 pid:551149968 iyr:2020 hcl:#6b5442\n" +
            "byr:1948\n" +
            "ecl:grn\n" +
            "hgt:152cm\n" +
            "\n" +
            "hgt:76in cid:113 iyr:2019 eyr:2023 hcl:#888785 pid:131239468 ecl:grn\n" +
            "byr:1994\n" +
            "\n" +
            "ecl:oth cid:240 hcl:#bed757 byr:2027 eyr:2021 pid:#ffa971 iyr:2022\n" +
            "\n" +
            "cid:204 iyr:2011\n" +
            "ecl:blu hgt:169cm byr:1985 eyr:2020 hcl:#18171d\n" +
            "\n" +
            "ecl:hzl iyr:2012 cid:344 hcl:#7d3b0c\n" +
            "hgt:190cm pid:599490023 byr:1954 eyr:2023\n" +
            "\n" +
            "cid:333\n" +
            "eyr:1971 hgt:193cm\n" +
            "ecl:#12421d hcl:#7d3b0c iyr:1991 pid:#7149ad byr:2008\n" +
            "\n" +
            "iyr:2014\n" +
            "hgt:151cm pid:190259199 eyr:2021 ecl:blu\n" +
            "byr:1975 hcl:#ceb3a1\n" +
            "\n" +
            "hgt:164cm ecl:oth hcl:#c0946f pid:427760590 eyr:2023 iyr:2012\n" +
            "byr:1979\n" +
            "\n" +
            "hgt:193cm iyr:2023 ecl:#213711 hcl:z\n" +
            "pid:23861701\n" +
            "byr:2020\n" +
            "eyr:1924\n" +
            "\n" +
            "pid:450691994 cid:191\n" +
            "eyr:2028\n" +
            "byr:1972 ecl:oth hgt:168cm hcl:#888785\n" +
            "\n" +
            "iyr:2013 hcl:#18171d hgt:170cm ecl:blu\n" +
            "pid:040253250 eyr:2024\n" +
            "byr:1954 cid:340\n" +
            "\n" +
            "cid:185 byr:1956 eyr:2029 pid:454637740 ecl:hzl hcl:#efcc98 iyr:2019 hgt:73in\n" +
            "\n" +
            "hcl:#efcc98\n" +
            "hgt:176cm\n" +
            "ecl:hzl cid:113 pid:747653564 iyr:2016\n" +
            "eyr:2020 byr:1945\n" +
            "\n" +
            "hgt:69in cid:264 byr:1971 hcl:#733820 ecl:amb pid:086130104\n" +
            "iyr:2011\n" +
            "eyr:2022\n" +
            "\n" +
            "iyr:2010\n" +
            "eyr:2034\n" +
            "pid:501068596\n" +
            "hgt:109 hcl:z byr:2018 cid:326 ecl:lzr\n" +
            "\n" +
            "pid:955229652\n" +
            "eyr:2027 cid:175\n" +
            "byr:1950 iyr:2010 ecl:gry hcl:#866857 hgt:177cm\n" +
            "\n" +
            "ecl:amb hcl:#888785 eyr:2020\n" +
            "hgt:172cm byr:1991\n" +
            "pid:556956304\n" +
            "\n" +
            "byr:1930\n" +
            "eyr:2011\n" +
            "pid:734176827\n" +
            "ecl:brn hgt:182cm\n" +
            "hcl:z\n" +
            "\n" +
            "hcl:#a97842\n" +
            "pid:040278061 ecl:brn hgt:168cm cid:194\n" +
            "byr:1973\n" +
            "iyr:2016 eyr:2027\n" +
            "\n" +
            "hcl:#623a2f\n" +
            "eyr:2023\n" +
            "ecl:blu iyr:2016 pid:844348663 byr:1997 hgt:179cm\n" +
            "\n" +
            "hgt:188cm hcl:#a97842 byr:1972\n" +
            "ecl:hzl pid:912948357 eyr:2026 iyr:2025\n" +
            "\n" +
            "iyr:2011 eyr:2025\n" +
            "cid:286\n" +
            "pid:084736292\n" +
            "byr:1936\n" +
            "ecl:oth hcl:#a97842 hgt:166cm\n" +
            "\n" +
            "iyr:2012 ecl:blu hgt:159cm byr:1980 eyr:2024 pid:811644928 cid:105 hcl:#7d3b0c\n" +
            "\n" +
            "pid:530452683 hcl:#341e13\n" +
            "iyr:2011\n" +
            "hgt:163cm ecl:oth\n" +
            "cid:309 byr:1940\n" +
            "\n" +
            "ecl:hzl\n" +
            "pid:144377866\n" +
            "hcl:#18171d hgt:193cm\n" +
            "iyr:2013 eyr:2028\n" +
            "\n" +
            "pid:868386570\n" +
            "ecl:brn\n" +
            "hgt:161cm hcl:#18171d\n" +
            "byr:1956\n" +
            "iyr:2017\n" +
            "cid:307\n" +
            "\n" +
            "iyr:2019 eyr:2026 ecl:brn\n" +
            "hcl:#866857 byr:1993 cid:299\n" +
            "pid:603503348 hgt:186cm\n" +
            "\n" +
            "iyr:2014\n" +
            "pid:852954158 hgt:73in byr:2021\n" +
            "eyr:2020 hcl:#a97842 cid:260 ecl:oth\n" +
            "\n" +
            "hgt:164cm eyr:2025 pid:113005290 byr:1955 ecl:blu iyr:2017 hcl:#b6652a\n" +
            "\n" +
            "cid:179 iyr:2015\n" +
            "pid:317467924 eyr:2025 ecl:gry byr:1996 hgt:180cm hcl:#a55f97\n" +
            "\n" +
            "hgt:172cm hcl:#efcc98 cid:53 ecl:grn iyr:2016\n" +
            "byr:1991 pid:337133478\n" +
            "eyr:2025\n" +
            "\n" +
            "hgt:150 iyr:2008\n" +
            "pid:#3e66a7 ecl:#8b3133 eyr:2040 byr:2012 hcl:802d16\n" +
            "\n" +
            "pid:577607614 byr:1924 hgt:173cm hcl:#341e13 eyr:2026 ecl:amb\n" +
            "iyr:2013\n" +
            "\n" +
            "eyr:2020 iyr:2011 hgt:175cm hcl:316607 pid:738554684\n" +
            "byr:2029 ecl:dne\n" +
            "\n" +
            "hgt:179cm iyr:2016\n" +
            "pid:178cm byr:2015\n" +
            "ecl:gry\n" +
            "hcl:#341e13\n" +
            "eyr:1986\n" +
            "\n" +
            "byr:2005 iyr:2028 ecl:#7be9b8 eyr:1941 pid:#e7e9cb hgt:177in cid:67 hcl:#602927\n" +
            "\n" +
            "ecl:#0d50e6\n" +
            "pid:192cm iyr:2014 eyr:2027 hgt:73cm cid:162 hcl:93ea2f\n" +
            "byr:1958\n" +
            "\n" +
            "hcl:z\n" +
            "cid:292 hgt:184in eyr:2001 pid:7218132701 byr:2020\n" +
            "ecl:grt iyr:2014\n" +
            "\n" +
            "ecl:gry\n" +
            "hcl:#fffffd\n" +
            "eyr:2026 iyr:2013\n" +
            "pid:117261833\n" +
            "\n" +
            "pid:780384540 ecl:gry cid:52 eyr:2020 hgt:193cm hcl:#4ae223 iyr:2017\n" +
            "byr:1984\n" +
            "\n" +
            "ecl:hzl\n" +
            "pid:218314886 eyr:2030 byr:1948 hcl:#c0946f hgt:185cm iyr:2013\n" +
            "\n" +
            "pid:175cm cid:340 ecl:blu hcl:#cfa07d eyr:2036 iyr:2018 byr:2018 hgt:70cm\n" +
            "\n" +
            "byr:1953 hgt:164cm ecl:hzl\n" +
            "pid:488831953 iyr:2010\n" +
            "hcl:#fffffd\n" +
            "\n" +
            "byr:1961 hgt:165cm pid:506597451\n" +
            "cid:122 eyr:2020 hcl:#cfa07d ecl:gry\n" +
            "iyr:2016\n" +
            "\n" +
            "iyr:1970\n" +
            "eyr:2040\n" +
            "byr:2008\n" +
            "hgt:188\n" +
            "ecl:#b00a46 hcl:#fffffd\n" +
            "\n" +
            "hgt:179cm\n" +
            "byr:1972 eyr:2026\n" +
            "cid:62 ecl:oth\n" +
            "pid:996355557 iyr:2013 hcl:#a97842\n" +
            "\n" +
            "ecl:amb eyr:2026 byr:1936 pid:812982189 hgt:158cm hcl:#888785 iyr:2010\n" +
            "\n" +
            "iyr:2020\n" +
            "hcl:#7d3b0c hgt:160cm\n" +
            "pid:336806720\n" +
            "eyr:2024 ecl:#7e0ae0 byr:1992\n" +
            "\n" +
            "eyr:2036 pid:178cm hcl:z\n" +
            "hgt:133 byr:2009 ecl:dne cid:127\n" +
            "\n" +
            "byr:1938 hcl:#fd309a\n" +
            "cid:104 iyr:2015 eyr:2022 pid:201047563\n" +
            "hgt:160cm ecl:hzl\n" +
            "\n" +
            "byr:2023 pid:25086180 hgt:160cm cid:180 hcl:z ecl:grt eyr:2038 iyr:2022\n" +
            "\n" +
            "ecl:grn hgt:167cm\n" +
            "byr:2023 iyr:2026 eyr:1928 hcl:z\n" +
            "\n" +
            "hcl:#efcc98 hgt:187cm byr:1925\n" +
            "ecl:grn\n" +
            "pid:753746076 iyr:2017\n" +
            "eyr:2021\n" +
            "\n" +
            "iyr:2017\n" +
            "byr:1934 ecl:grn eyr:2021 hgt:163cm\n" +
            "pid:688172460 hcl:#b6652a\n" +
            "\n" +
            "hcl:#c0946f iyr:2018 ecl:blu pid:676564085\n" +
            "hgt:184cm cid:152 byr:1980 eyr:2023\n" +
            "\n" +
            "ecl:grt hgt:70cm iyr:2022 hcl:58716b byr:2010\n" +
            "pid:60834390 eyr:2037\n" +
            "\n" +
            "iyr:2028 pid:270499403\n" +
            "ecl:xry eyr:1947 hgt:152cm byr:2025\n" +
            "\n" +
            "pid:091281559 hcl:#733820\n" +
            "hgt:166cm\n" +
            "eyr:2021 ecl:grn cid:327 byr:1928\n" +
            "iyr:2014\n" +
            "\n" +
            "eyr:2025 ecl:grn byr:1938 hcl:#ceb3a1\n" +
            "cid:234\n" +
            "pid:549433891\n" +
            "hgt:172cm iyr:2016\n" +
            "\n" +
            "hcl:#c0946f hgt:173cm iyr:2014 eyr:2030 ecl:blu byr:1965\n" +
            "pid:696577272\n" +
            "\n" +
            "hgt:154cm eyr:2030\n" +
            "pid:475642195 byr:1920 iyr:2013 hcl:#866857 ecl:blu\n" +
            "\n" +
            "pid:518398763 iyr:2010\n" +
            "eyr:2020\n" +
            "hgt:183cm\n" +
            "ecl:brn byr:1921 hcl:#18171d\n" +
            "\n" +
            "eyr:2023 pid:614116723 hcl:#7d3b0c ecl:hzl\n" +
            "iyr:2016 hgt:189cm byr:2000\n" +
            "\n" +
            "ecl:oth hgt:178cm hcl:#733820 byr:2001 pid:862420089 eyr:2023\n" +
            "\n" +
            "pid:851985534 eyr:2028 hcl:#18171d ecl:oth cid:238 byr:2001\n" +
            "iyr:2019 hgt:166cm\n" +
            "\n" +
            "byr:1927\n" +
            "hgt:170cm\n" +
            "pid:246933107\n" +
            "ecl:amb iyr:2015\n" +
            "cid:166 eyr:2027 hcl:#b6652a\n" +
            "\n" +
            "byr:1929\n" +
            "hcl:#7d3b0c\n" +
            "cid:263 pid:317156081 hgt:165cm eyr:2031 iyr:1980\n" +
            "\n" +
            "hcl:#866857 eyr:2021 hgt:179cm pid:206504353 cid:84 ecl:gry iyr:2012 byr:1952\n" +
            "\n" +
            "byr:1986 ecl:hzl\n" +
            "hcl:#a97842\n" +
            "iyr:2015 hgt:152cm pid:722601936 eyr:2025\n" +
            "\n" +
            "byr:1921\n" +
            "pid:563550743 iyr:2015 ecl:hzl\n" +
            "eyr:2026 hcl:#fffd7b\n" +
            "\n" +
            "ecl:hzl\n" +
            "hcl:#888785 cid:268 byr:1926 hgt:176cm pid:321394231 eyr:2021 iyr:2014\n" +
            "\n" +
            "eyr:2021 cid:225\n" +
            "pid:770796086\n" +
            "ecl:gry byr:1961\n" +
            "hgt:154cm\n" +
            "hcl:#6b5442\n" +
            "iyr:2011\n" +
            "\n" +
            "eyr:2028 iyr:1961 byr:2016\n" +
            "cid:98 pid:587360691 hgt:70cm ecl:#ceaf1f\n" +
            "hcl:#c0b6db\n" +
            "\n" +
            "byr:1978\n" +
            "eyr:2022 hgt:184cm hcl:#7d3b0c\n" +
            "cid:271\n" +
            "ecl:amb pid:235352975\n" +
            "iyr:2010\n" +
            "\n" +
            "eyr:2026 pid:2844744\n" +
            "iyr:1958 byr:2017 hcl:z\n" +
            "hgt:192in\n" +
            "ecl:#971530\n" +
            "\n" +
            "iyr:2020\n" +
            "byr:1960 eyr:2028 cid:162 pid:491912610 hcl:#fffffd hgt:59in\n" +
            "\n" +
            "iyr:2012 pid:365229485 ecl:amb byr:1933 hcl:#18171d eyr:2024\n" +
            "\n" +
            "hgt:193cm pid:473100400\n" +
            "hcl:#efcc98\n" +
            "cid:201 eyr:2020 byr:1969 ecl:gry iyr:2016\n" +
            "\n" +
            "eyr:2025 pid:137807160 iyr:2014\n" +
            "ecl:grn byr:1944 hgt:168cm hcl:#ceb3a1\n" +
            "\n" +
            "byr:2008 ecl:xry\n" +
            "iyr:2012 hcl:#efcc98 eyr:2028 pid:272344138\n" +
            "\n" +
            "eyr:2024 pid:959415175 cid:148 hcl:#efcc98\n" +
            "byr:1977 hgt:179cm ecl:amb\n" +
            "\n" +
            "pid:253742161 ecl:hzl hcl:#602927\n" +
            "eyr:2021 hgt:191cm byr:1925 iyr:2010\n" +
            "\n" +
            "ecl:amb hcl:#341e13\n" +
            "eyr:2024 iyr:2017\n" +
            "byr:1975\n" +
            "pid:838040028 hgt:172cm\n" +
            "\n" +
            "hgt:172in\n" +
            "pid:311113967 iyr:2015 cid:111 eyr:2023 ecl:oth byr:2003 hcl:#866857\n" +
            "\n" +
            "hcl:#888785 byr:1978 hgt:64in pid:442064310 eyr:2021\n" +
            "iyr:2011 ecl:hzl\n" +
            "\n" +
            "eyr:2021 byr:1988 hcl:#a97842\n" +
            "pid:290578586 ecl:hzl hgt:174cm iyr:2020\n" +
            "\n" +
            "byr:1998 iyr:2020 hgt:163cm ecl:oth eyr:2025\n" +
            "hcl:#6b5442 pid:913461954\n" +
            "\n" +
            "hgt:173cm hcl:#18171d\n" +
            "eyr:2029 ecl:brn cid:313 byr:1980\n" +
            "iyr:2011 pid:810497375\n" +
            "\n" +
            "byr:1975 hgt:153cm eyr:2027 hcl:#fffffd pid:857730031\n" +
            "ecl:gry iyr:2020\n" +
            "\n" +
            "hcl:#18171d ecl:hzl\n" +
            "pid:185778821 hgt:178 iyr:2014 eyr:2028 byr:1974\n" +
            "\n" +
            "iyr:2015 hgt:163in hcl:#c0946f ecl:#4844a6 byr:1979 pid:124626004\n" +
            "\n" +
            "eyr:2024\n" +
            "pid:737015681 byr:1952\n" +
            "ecl:hzl iyr:2019\n" +
            "hgt:192cm hcl:#cfa07d\n" +
            "\n" +
            "pid:2986469633 byr:2025 hgt:66cm hcl:z eyr:2011 iyr:2027 cid:311\n" +
            "\n" +
            "byr:1962\n" +
            "eyr:2032\n" +
            "ecl:lzr iyr:2014\n" +
            "hgt:70cm pid:94309916\n" +
            "hcl:#fffffd\n" +
            "\n" +
            "cid:350 hcl:#602927 iyr:2019 hgt:178cm\n" +
            "pid:172238204 byr:1949 ecl:hzl\n" +
            "eyr:2028\n" +
            "\n" +
            "hgt:153cm\n" +
            "hcl:#ceb3a1\n" +
            "ecl:grn\n" +
            "byr:1997\n" +
            "pid:266747822\n" +
            "iyr:2011 eyr:2022\n" +
            "\n" +
            "pid:839681159 hgt:150cm eyr:2024 hcl:4d6414\n" +
            "ecl:blu\n" +
            "iyr:2018 byr:1988\n" +
            "\n" +
            "byr:1930 iyr:2011 pid:352711700 hgt:174cm cid:67 eyr:2020 ecl:hzl hcl:#6b5442\n" +
            "\n" +
            "byr:1949 iyr:2013 hcl:#623a2f eyr:2030\n" +
            "hgt:176cm\n" +
            "\n" +
            "hgt:164cm eyr:2026 hcl:#866857\n" +
            "iyr:2018 pid:922679610 byr:1974\n" +
            "ecl:brn\n" +
            "cid:114\n" +
            "\n" +
            "eyr:2038 cid:317\n" +
            "hgt:166in pid:0384056779 byr:2013 iyr:2021\n" +
            "ecl:xry\n" +
            "\n" +
            "cid:83 hgt:166cm eyr:2026 iyr:2018 byr:1994 ecl:brn pid:858360477 hcl:#ceb3a1\n" +
            "\n" +
            "hgt:169cm eyr:2020\n" +
            "pid:110129489 byr:1958\n" +
            "ecl:oth hcl:#7d3b0c\n" +
            "iyr:2011\n" +
            "\n" +
            "cid:279\n" +
            "iyr:2019 byr:1995 eyr:2026 ecl:hzl\n" +
            "hcl:#7d3b0c hgt:185cm pid:085427066\n" +
            "\n" +
            "hcl:#c0946f\n" +
            "iyr:2011 eyr:2027\n" +
            "ecl:amb\n" +
            "byr:1943 pid:060674566 hgt:183in\n" +
            "\n" +
            "hgt:156cm hcl:#c0946f pid:242827141\n" +
            "cid:152\n" +
            "iyr:2018\n" +
            "eyr:2025 byr:1963\n" +
            "\n" +
            "byr:1925 cid:168 eyr:2020 hcl:#cfa07d iyr:2011 ecl:brn hgt:150cm pid:740118192\n" +
            "\n" +
            "ecl:oth byr:1951 eyr:2025 cid:213\n" +
            "iyr:2020\n" +
            "hgt:154cm\n" +
            "\n" +
            "eyr:2025 iyr:2018 ecl:grn cid:91 byr:1925\n" +
            "hgt:164cm hcl:#18171d\n" +
            "\n" +
            "byr:1997\n" +
            "iyr:2018 eyr:2023 hcl:#602927 pid:251296833 ecl:blu\n" +
            "hgt:185cm\n" +
            "\n" +
            "hgt:168cm pid:556895048\n" +
            "hcl:#341e13 ecl:oth eyr:2020 cid:64 byr:1940\n" +
            "\n" +
            "byr:1996 pid:821204904 cid:250 ecl:amb eyr:2026 hgt:185cm iyr:2019\n" +
            "\n" +
            "ecl:grn hcl:#b6652a iyr:2013\n" +
            "eyr:2028 hgt:157cm\n" +
            "byr:1925 pid:158cm\n" +
            "\n" +
            "hgt:190cm iyr:2019 ecl:oth eyr:2028 hcl:#341e13 cid:334 pid:258135663 byr:1972\n" +
            "\n" +
            "byr:1936 hgt:76in pid:748344702 cid:335\n" +
            "eyr:2027 hcl:#a97842 ecl:amb iyr:2015\n" +
            "\n" +
            "hcl:z hgt:66cm eyr:2029\n" +
            "pid:#1589e0 iyr:2019 ecl:hzl\n" +
            "\n" +
            "hcl:#733820 ecl:amb\n" +
            "iyr:2013\n" +
            "hgt:188cm byr:1955 pid:125663066 eyr:2020 cid:179\n" +
            "\n" +
            "iyr:2017\n" +
            "hgt:185cm ecl:grn\n" +
            "cid:298 eyr:2030 hcl:#5b1c03\n" +
            "byr:1992 pid:092887457\n" +
            "\n" +
            "eyr:2032 ecl:grn hgt:82 iyr:2022\n" +
            "pid:180cm byr:2003\n" +
            "cid:55 hcl:z\n" +
            "\n" +
            "pid:257666411 eyr:2023 byr:1982 hgt:179cm hcl:#18171d ecl:brn iyr:2010\n" +
            "\n" +
            "iyr:2020\n" +
            "ecl:amb hcl:#18171d\n" +
            "pid:971402454 eyr:2028\n" +
            "\n" +
            "hcl:#efcc98 byr:1964 pid:577424639 eyr:2030 iyr:2010 ecl:brn hgt:169cm\n" +
            "cid:285\n" +
            "\n" +
            "ecl:amb byr:1958 hgt:159cm hcl:#efcc98 eyr:2024 iyr:2016\n" +
            "pid:029502840\n" +
            "\n" +
            "hcl:ac11eb\n" +
            "byr:2007 pid:0489471320 hgt:69cm iyr:2030 ecl:blu eyr:2033\n" +
            "\n" +
            "pid:3785138563 eyr:2020 iyr:2020\n" +
            "hcl:#966583 byr:2008 hgt:186cm ecl:gry\n" +
            "\n" +
            "iyr:2014 pid:868785127 eyr:2029\n" +
            "cid:220 hcl:#18171d ecl:blu byr:1948 hgt:171cm\n" +
            "\n" +
            "byr:1936\n" +
            "pid:433437105\n" +
            "hcl:#c0946f eyr:2020 iyr:2019 hgt:160cm ecl:brn\n" +
            "\n" +
            "iyr:2015 eyr:2024 hgt:176cm ecl:hzl\n" +
            "byr:1995 pid:101835436 hcl:#ceb3a1\n" +
            "\n" +
            "eyr:1959\n" +
            "hcl:#cfa07d iyr:2010 pid:9214728\n" +
            "ecl:#42fda0 hgt:71 byr:2022\n" +
            "\n" +
            "byr:1998 iyr:2011 cid:275 ecl:oth\n" +
            "pid:924517068 eyr:2024 hgt:191cm\n" +
            "hcl:#623a2f\n" +
            "\n" +
            "hgt:157 hcl:z\n" +
            "byr:1923 pid:#f6ce52 iyr:1975 ecl:lzr cid:100\n" +
            "\n" +
            "pid:565022102\n" +
            "eyr:2021 hcl:#efcc98\n" +
            "byr:1988 ecl:gry iyr:2012\n" +
            "\n" +
            "hgt:156cm\n" +
            "hcl:#b6652a eyr:2021 pid:969724332\n" +
            "cid:126 iyr:2016\n" +
            "ecl:hzl byr:1988\n" +
            "\n" +
            "ecl:blu hcl:#866857 hgt:153cm\n" +
            "pid:798083560\n" +
            "iyr:2015\n" +
            "byr:1981 eyr:2030\n" +
            "\n" +
            "iyr:2013 cid:103 hcl:#efcc98 eyr:2022 byr:1964 ecl:gry\n" +
            "hgt:161cm pid:950689613\n" +
            "\n" +
            "pid:4316019547\n" +
            "ecl:gmt\n" +
            "eyr:2029 byr:2011 iyr:2005 hgt:170cm cid:135\n" +
            "hcl:567fd8\n" +
            "\n" +
            "hcl:#6b5442 pid:843348901 byr:1960\n" +
            "hgt:156cm\n" +
            "eyr:2028 ecl:amb\n" +
            "\n" +
            "eyr:2027\n" +
            "pid:286247733 byr:2000 hgt:191cm\n" +
            "iyr:2014\n" +
            "hcl:#341e13 ecl:amb\n" +
            "\n" +
            "ecl:gmt byr:2005 hgt:182cm pid:376332625 hcl:z iyr:2021\n" +
            "eyr:1949\n" +
            "\n" +
            "hgt:184cm\n" +
            "byr:1940\n" +
            "cid:260 eyr:2030 ecl:brn\n" +
            "iyr:2011 pid:792881807\n" +
            "\n" +
            "iyr:1936 eyr:2021 cid:133 hcl:#623a2f byr:2003 pid:197167496\n" +
            "ecl:#8896de\n" +
            "\n" +
            "hgt:67in cid:110\n" +
            "byr:1951\n" +
            "pid:389358116 eyr:2028 iyr:2017\n" +
            "ecl:grn\n" +
            "\n" +
            "hgt:161cm\n" +
            "cid:215\n" +
            "pid:116325531 iyr:2019\n" +
            "eyr:2025 hcl:#18171d ecl:blu\n" +
            "byr:1951\n" +
            "\n" +
            "pid:787859682 hcl:#a97842 eyr:2020 byr:1948 hgt:190cm ecl:brn iyr:2020\n" +
            "\n" +
            "pid:034440951 hgt:73cm hcl:803e55\n" +
            "cid:350 byr:1985\n" +
            "ecl:#a18487 eyr:2031\n" +
            "iyr:1973\n" +
            "\n" +
            "hcl:#40ee86 ecl:brn\n" +
            "iyr:2016 byr:1922 hgt:150cm pid:449374426\n" +
            "\n" +
            "eyr:2040 hcl:260be4 pid:208681353 byr:2029 ecl:gry\n" +
            "hgt:178cm\n" +
            "\n" +
            "hcl:#18171d hgt:162cm byr:1983 eyr:2020 pid:328556776 iyr:2017 ecl:grn\n" +
            "\n" +
            "eyr:2029\n" +
            "hcl:#a97842\n" +
            "pid:#7bd019 iyr:2015\n" +
            "hgt:168cm byr:1926\n" +
            "ecl:grn\n" +
            "\n" +
            "ecl:grt eyr:2034 pid:640680934 hgt:189in cid:276 byr:1969 hcl:511eed iyr:2023\n" +
            "\n" +
            "eyr:2039 hgt:182in cid:145\n" +
            "hcl:4a259b iyr:2026\n" +
            "byr:2004\n" +
            "ecl:xry pid:#a3c9ea\n" +
            "\n" +
            "hcl:#866857\n" +
            "pid:615665716 ecl:blu hgt:164cm iyr:2020\n" +
            "byr:1948 eyr:2024 cid:286\n" +
            "\n" +
            "hcl:#b6652a hgt:59in eyr:2027\n" +
            "pid:752461325 ecl:oth\n" +
            "byr:1932 iyr:2019\n" +
            "\n" +
            "eyr:2030 byr:1936 ecl:hzl\n" +
            "iyr:2010 cid:263 pid:186570962 hcl:#888785\n" +
            "hgt:163cm\n" +
            "\n" +
            "byr:1949 ecl:blu\n" +
            "pid:407719342\n" +
            "eyr:2030\n" +
            "hcl:#b6652a iyr:2012\n" +
            "hgt:186cm\n" +
            "\n" +
            "pid:154cm ecl:amb byr:1944\n" +
            "eyr:2022\n" +
            "hcl:z iyr:2017\n" +
            "\n" +
            "byr:1980 hcl:#d2c954 iyr:2013 ecl:brn hgt:72in\n" +
            "eyr:2030\n" +
            "pid:017095362\n" +
            "\n" +
            "hgt:179cm\n" +
            "hcl:#ceb3a1 cid:61 eyr:2026\n" +
            "iyr:2011\n" +
            "pid:897403026 byr:1984\n" +
            "ecl:amb\n" +
            "\n" +
            "cid:150 hgt:181cm\n" +
            "eyr:2028 pid:894689339\n" +
            "hcl:#602927 byr:1933 ecl:grn iyr:2018\n" +
            "\n" +
            "pid:125553946 byr:1942 eyr:2026 hgt:193cm\n" +
            "iyr:2010 ecl:gry\n" +
            "hcl:z\n" +
            "\n" +
            "eyr:2013 pid:1213613355\n" +
            "ecl:#b08dca hgt:190in\n" +
            "hcl:06adb3 cid:303 iyr:2010\n" +
            "\n" +
            "iyr:2019 pid:255938897\n" +
            "eyr:2022 hgt:152cm\n" +
            "byr:1956 ecl:grn hcl:#ceb3a1\n" +
            "\n" +
            "eyr:2029\n" +
            "pid:670713784\n" +
            "iyr:2020 ecl:grn\n" +
            "hgt:155cm hcl:#6b5442 byr:2002\n" +
            "\n" +
            "byr:1925 hcl:#866857 pid:323449427 ecl:oth\n" +
            "eyr:2023 hgt:163cm iyr:2014\n" +
            "\n" +
            "pid:841608722 byr:1955 hgt:150cm ecl:blu eyr:2029\n" +
            "hcl:#6b5442\n" +
            "\n" +
            "eyr:2023 hcl:#efcc98\n" +
            "hgt:164cm ecl:gry\n" +
            "iyr:2018\n" +
            "byr:1993 pid:501920795\n" +
            "\n" +
            "eyr:2030\n" +
            "iyr:2019 hgt:73in hcl:#bf908a\n" +
            "byr:1961 ecl:blu cid:86 pid:436811356\n" +
            "\n" +
            "pid:#02516a hgt:131 iyr:1969 ecl:grt byr:2015\n" +
            "eyr:2010 hcl:z\n" +
            "\n" +
            "ecl:#25fb6c cid:239 pid:167cm iyr:2021\n" +
            "byr:2023 hgt:75cm\n" +
            "hcl:z eyr:1931\n" +
            "\n" +
            "pid:279251948\n" +
            "ecl:oth hcl:#6b5442\n" +
            "byr:1943 iyr:2015 hgt:173cm eyr:2039\n" +
            "\n" +
            "byr:1935\n" +
            "iyr:2013 hgt:151cm hcl:#b6652a\n" +
            "ecl:grn\n" +
            "eyr:2023 pid:741958450\n" +
            "\n" +
            "hcl:6beab7 byr:1986\n" +
            "hgt:85\n" +
            "iyr:2012 pid:#d98df3 eyr:2035\n" +
            "ecl:dne\n" +
            "\n" +
            "byr:1929\n" +
            "pid:764478810 ecl:grn\n" +
            "hcl:#866857 iyr:2019 hgt:155cm eyr:2022 cid:277\n" +
            "\n" +
            "hgt:155cm pid:450816410 eyr:2030 cid:165 byr:1969 ecl:blu hcl:#866857 iyr:2019\n" +
            "\n" +
            "cid:330 pid:168777528 eyr:2024 ecl:blu hcl:#341e13\n" +
            "hgt:178cm iyr:2013\n" +
            "byr:1921\n" +
            "\n" +
            "eyr:2037 iyr:1973 hcl:a4ebf3\n" +
            "pid:161cm\n" +
            "ecl:oth hgt:64cm cid:62\n" +
            "\n" +
            "cid:235\n" +
            "hcl:538f8a hgt:70cm\n" +
            "iyr:1970 pid:177837127\n" +
            "ecl:#95700d byr:2003\n" +
            "\n" +
            "ecl:hzl pid:375018246 hgt:161cm\n" +
            "iyr:2011 eyr:2029 hcl:#c0946f\n" +
            "byr:1956\n" +
            "\n" +
            "hcl:#888785\n" +
            "iyr:2016\n" +
            "pid:161cm byr:1977 ecl:#0188d8 eyr:2029\n" +
            "cid:104 hgt:63in\n" +
            "\n" +
            "byr:1979 eyr:2020 hcl:#ceb3a1 ecl:amb pid:752141341 hgt:150cm iyr:2010\n" +
            "\n" +
            "cid:274 byr:1928 iyr:2018 eyr:2023 hcl:#a97842 hgt:173cm pid:186060112 ecl:gry\n" +
            "\n" +
            "hcl:#341e13\n" +
            "ecl:blu iyr:2011\n" +
            "hgt:190cm cid:292 pid:974271891 eyr:2020 byr:1927\n" +
            "\n" +
            "hcl:#fffffd eyr:2025\n" +
            "ecl:brn byr:1923 iyr:2011\n" +
            "pid:037981552\n" +
            "\n" +
            "ecl:blu pid:412817852 hgt:150cm iyr:2026\n" +
            "byr:2026\n" +
            "eyr:2020\n" +
            "\n" +
            "ecl:brn byr:1988 eyr:2026\n" +
            "hgt:178cm pid:008152501\n" +
            "hcl:#602927\n" +
            "iyr:2020\n" +
            "\n" +
            "ecl:brn pid:877401308 byr:1923 cid:154\n" +
            "hgt:170cm\n" +
            "hcl:#fffffd\n" +
            "iyr:2014\n" +
            "eyr:2022\n" +
            "\n" +
            "cid:56 hcl:ee020e pid:590581021 iyr:2018 hgt:72cm byr:2007\n" +
            "eyr:1964 ecl:oth\n" +
            "\n" +
            "eyr:2029\n" +
            "iyr:2012 ecl:oth\n" +
            "hgt:185cm cid:235\n" +
            "byr:2002\n" +
            "pid:064901580\n" +
            "\n" +
            "byr:1956 hcl:#6c1a8c pid:497814257\n" +
            "eyr:1964 hgt:155cm ecl:gmt iyr:2030\n" +
            "\n" +
            "byr:1935 hgt:171cm cid:253 pid:033393224 hcl:#c0946f iyr:2012\n" +
            "ecl:blu eyr:2025\n" +
            "\n" +
            "byr:1977 hcl:#602927 cid:175 iyr:2010\n" +
            "pid:9391986394 hgt:65in eyr:2026\n" +
            "ecl:amb\n" +
            "\n" +
            "iyr:2011 hgt:158cm ecl:#31cae1 byr:1958 hcl:b94ad1\n" +
            "eyr:2023 pid:#400a21\n" +
            "\n" +
            "hcl:e205b0 pid:84195182 byr:2012 eyr:2037 ecl:zzz hgt:75cm iyr:2030\n" +
            "\n" +
            "pid:102379515\n" +
            "byr:1971\n" +
            "hgt:169cm\n" +
            "ecl:amb\n" +
            "eyr:2020 hcl:#cfa07d iyr:2017\n" +
            "\n" +
            "pid:236611157\n" +
            "eyr:2020 hcl:#b6652a\n" +
            "iyr:2017 cid:194 byr:2001 hgt:169cm ecl:gry\n" +
            "\n" +
            "iyr:2012 hcl:a256b5 eyr:2040 cid:62 hgt:177in byr:2010\n" +
            "\n" +
            "eyr:2028 byr:2009 iyr:2020 ecl:brn\n" +
            "pid:12371575 hcl:#866857 hgt:190cm\n" +
            "\n" +
            "byr:1965 eyr:2028\n" +
            "pid:402013776 hcl:#bc4e9e cid:183 hgt:150cm iyr:2015\n" +
            "\n" +
            "pid:0269051559\n" +
            "byr:1936 hcl:z ecl:#ff0ab9\n" +
            "iyr:2014 eyr:2031\n" +
            "cid:346 hgt:153in\n" +
            "\n" +
            "hcl:#18171d iyr:1929 hgt:157cm\n" +
            "eyr:2036 byr:1970\n" +
            "ecl:amb\n" +
            "\n" +
            "hcl:#733820\n" +
            "eyr:2022\n" +
            "pid:096076686\n" +
            "iyr:2010\n" +
            "hgt:192cm\n" +
            "byr:1957\n" +
            "\n" +
            "hcl:#ceb3a1 ecl:brn iyr:2013\n" +
            "eyr:2025\n" +
            "byr:1953 pid:751516675\n" +
            "hgt:175cm\n" +
            "\n" +
            "byr:1928\n" +
            "eyr:2027\n" +
            "cid:85\n" +
            "hgt:179cm ecl:oth\n" +
            "pid:169307999 hcl:#3e07af iyr:2010\n" +
            "\n" +
            "hgt:60cm byr:2008 hcl:z\n" +
            "eyr:1965 pid:167cm\n" +
            "cid:106\n" +
            "iyr:1930\n" +
            "\n" +
            "hcl:#1099d9 ecl:amb pid:638820661 iyr:2014\n" +
            "byr:1998 eyr:2025\n" +
            "hgt:162cm\n" +
            "\n" +
            "ecl:amb\n" +
            "eyr:2022 hcl:#623a2f byr:1956\n" +
            "hgt:154cm\n" +
            "iyr:2010 pid:717452826\n" +
            "\n" +
            "hcl:fc9ba5\n" +
            "iyr:1928\n" +
            "eyr:2029 pid:54503219\n" +
            "byr:2020\n" +
            "ecl:#d2155a hgt:124\n" +
            "\n" +
            "eyr:2027\n" +
            "hcl:#7d3b0c hgt:178 ecl:#63b8e6 iyr:2015 byr:1954\n" +
            "\n" +
            "ecl:oth byr:1970\n" +
            "pid:833178609 hcl:#c0946f iyr:2016 cid:81 eyr:1976\n" +
            "hgt:69in\n" +
            "\n" +
            "hcl:#0cf4b8 pid:499271062 hgt:62in ecl:hzl iyr:2016 byr:1922\n" +
            "eyr:2022\n" +
            "\n" +
            "byr:1994\n" +
            "eyr:2029 hgt:174cm hcl:#efcc98\n" +
            "ecl:amb\n" +
            "iyr:2019 pid:297210449\n" +
            "\n" +
            "ecl:hzl\n" +
            "eyr:2026 iyr:2017 hcl:#a97842 hgt:162cm\n" +
            "byr:1950\n" +
            "\n" +
            "pid:091886000 hgt:179cm byr:1975 eyr:2020 cid:326\n" +
            "ecl:oth\n" +
            "iyr:2015 hcl:#a97842\n" +
            "\n" +
            "hcl:#efcc98 hgt:176cm byr:1940 iyr:2016 ecl:brn pid:514758507 eyr:2024 cid:313\n" +
            "\n" +
            "eyr:2026 byr:1980\n" +
            "hgt:155cm\n" +
            "iyr:2013 pid:367909831 ecl:oth\n" +
            "\n" +
            "byr:1965\n" +
            "eyr:2021 iyr:2017\n" +
            "hgt:185cm\n" +
            "hcl:#a97842 ecl:hzl pid:238901177\n" +
            "\n" +
            "hgt:156cm pid:916654189\n" +
            "byr:1943 eyr:2022 ecl:amb hcl:#341e13 iyr:2016\n" +
            "\n" +
            "cid:305 iyr:2013\n" +
            "eyr:2029 hgt:163cm ecl:blu\n" +
            "hcl:#fffffd pid:944033881\n" +
            "byr:1952\n" +
            "\n" +
            "pid:638190538\n" +
            "hcl:#866857 ecl:brn\n" +
            "eyr:2030 iyr:2016 cid:78 byr:1943 hgt:186cm\n" +
            "\n" +
            "eyr:2024 iyr:2015\n" +
            "pid:231006970\n" +
            "cid:312 byr:2000 hcl:#623a2f hgt:190cm ecl:brn\n" +
            "\n" +
            "ecl:#f89e87\n" +
            "hcl:#fffffd hgt:166 cid:215\n" +
            "iyr:1961\n" +
            "eyr:2027 pid:314310197 byr:1977\n" +
            "\n" +
            "hcl:z eyr:1995 pid:951911095 hgt:154cm\n" +
            "ecl:xry\n" +
            "cid:154 byr:2023\n" +
            "\n" +
            "hgt:66in hcl:#866857\n" +
            "ecl:brn\n" +
            "pid:328148585 byr:1984 eyr:2024\n" +
            "\n" +
            "pid:456453839\n" +
            "eyr:2024 hcl:#fffffd byr:1990 ecl:amb\n" +
            "\n" +
            "eyr:2030 cid:149 pid:983735096 hgt:179cm iyr:2014 byr:1957 ecl:gry hcl:#341e13\n" +
            "\n" +
            "byr:2001 hgt:157cm\n" +
            "ecl:hzl eyr:2021\n" +
            "hcl:#ceb3a1\n" +
            "pid:558527031 iyr:2018\n" +
            "\n" +
            "hgt:122 ecl:oth hcl:z\n" +
            "pid:384664729\n" +
            "iyr:2012 cid:298 eyr:2023\n" +
            "\n" +
            "ecl:utc eyr:2024\n" +
            "hgt:162in iyr:2018 pid:1722490341 byr:2027\n" +
            "hcl:#18171d\n" +
            "\n" +
            "ecl:gry iyr:2017 hcl:#602927 cid:303 byr:1950\n" +
            "pid:509264482 eyr:2030\n" +
            "hgt:164cm\n" +
            "\n" +
            "hgt:192cm pid:967128169 iyr:2019 ecl:blu eyr:2024 hcl:#fffffd byr:1949 cid:301\n" +
            "\n" +
            "ecl:blu\n" +
            "cid:71 hgt:164cm eyr:2022 hcl:#cfa07d pid:750303088\n" +
            "byr:1949 iyr:2014\n" +
            "\n" +
            "iyr:2014\n" +
            "pid:401425898 byr:1981\n" +
            "hcl:#7d3b0c hgt:167cm eyr:2028\n" +
            "\n" +
            "hcl:#602927 hgt:160cm iyr:2014\n" +
            "eyr:2023 byr:1940 pid:748539736 ecl:amb\n" +
            "\n" +
            "eyr:2025\n" +
            "hcl:#c0946f pid:325296854 iyr:2020\n" +
            "hgt:76cm ecl:amb byr:1921\n" +
            "\n" +
            "hgt:190cm\n" +
            "iyr:2011 pid:082777116\n" +
            "byr:1979 cid:73 ecl:oth hcl:#6b5442 eyr:2021\n" +
            "\n" +
            "eyr:2029 ecl:amb hgt:151cm pid:144881592 byr:1964 hcl:#efcc98 iyr:2012\n" +
            "\n" +
            "hcl:#efcc98\n" +
            "iyr:2019\n" +
            "eyr:2023 byr:1999 pid:645291123\n" +
            "ecl:brn\n" +
            "\n" +
            "eyr:2029 pid:922956941 hcl:#623a2f byr:1934\n" +
            "ecl:grn hgt:151cm\n" +
            "iyr:2019\n" +
            "\n" +
            "byr:1992 ecl:brn\n" +
            "hcl:#a97842\n" +
            "pid:269079906 hgt:187cm\n" +
            "iyr:2016 cid:218\n" +
            "\n" +
            "byr:1951 ecl:oth eyr:2026 hgt:185cm\n" +
            "cid:82 hcl:#7d3b0c\n" +
            "iyr:2020 pid:052476816\n" +
            "\n" +
            "eyr:2026\n" +
            "cid:319 iyr:2020\n" +
            "ecl:brn hcl:#888785\n" +
            "hgt:172cm pid:327064207 byr:1956\n" +
            "\n" +
            "hgt:178cm\n" +
            "pid:638854420 byr:1995 eyr:2030 ecl:gry hcl:#7d3b0c iyr:2018\n" +
            "\n" +
            "iyr:2026 hcl:#b6652a\n" +
            "byr:1946\n" +
            "hgt:186in pid:622875187 eyr:2028 ecl:gry cid:140\n" +
            "\n" +
            "byr:1931 ecl:oth eyr:2030\n" +
            "pid:437813485\n" +
            "hgt:181cm\n" +
            "hcl:#efcc98 iyr:2018\n" +
            "\n" +
            "byr:1999\n" +
            "ecl:amb\n" +
            "hgt:160cm iyr:2013 hcl:#b6652a pid:043039693\n" +
            "eyr:2022\n" +
            "\n" +
            "byr:2025\n" +
            "pid:#fd7ad7 eyr:2025 hgt:63in\n" +
            "ecl:oth iyr:2010 hcl:#b6652a\n" +
            "\n" +
            "ecl:grn\n" +
            "byr:1939 eyr:2025 hgt:171cm cid:134 iyr:2020 pid:090346629\n" +
            "hcl:#cfa07d\n" +
            "\n" +
            "hcl:z\n" +
            "eyr:2031 cid:74\n" +
            "pid:50216290 ecl:utc iyr:2030\n" +
            "hgt:176in\n" +
            "\n" +
            "byr:1971 ecl:brn hgt:190cm pid:791682756 hcl:#fffffd\n" +
            "iyr:2020 eyr:2027\n" +
            "\n" +
            "iyr:1931 byr:2025 hgt:76cm pid:735796617 eyr:2040 ecl:utc hcl:#c0946f\n" +
            "\n" +
            "hgt:163cm\n" +
            "hcl:#18171d\n" +
            "ecl:hzl\n" +
            "pid:628854394 cid:311 iyr:2020 eyr:2027\n" +
            "\n" +
            "hcl:z\n" +
            "ecl:amb pid:#a8f973 hgt:94\n" +
            "eyr:2027 byr:2020 iyr:2012 cid:202\n" +
            "\n" +
            "pid:086190379 byr:1931 ecl:blu iyr:2010 eyr:2027 hgt:175cm\n" +
            "\n" +
            "ecl:#0dafcd byr:2025 iyr:2021 eyr:1970 hgt:63cm cid:260 hcl:75300a pid:208921120\n" +
            "\n" +
            "pid:024722981 iyr:2011 hgt:193cm hcl:#efcc98 ecl:blu byr:2001\n" +
            "\n" +
            "byr:2027\n" +
            "cid:123\n" +
            "ecl:xry hgt:183cm iyr:2019 eyr:2026\n" +
            "hcl:#c0946f\n" +
            "pid:380513483\n" +
            "\n" +
            "eyr:2028 pid:302044900 iyr:2011 byr:1938 hgt:190cm ecl:amb hcl:#c0946f\n" +
            "\n" +
            "eyr:2024 pid:672033747 byr:1931\n" +
            "iyr:2020 hcl:#f01aed ecl:brn\n" +
            "\n" +
            "hgt:184cm hcl:#efcc98 pid:391597648\n" +
            "iyr:2020 ecl:gry\n" +
            "byr:1961\n" +
            "\n" +
            "iyr:2013 hgt:191cm byr:1935 eyr:2028 hcl:#ceb3a1 cid:195 ecl:brn\n" +
            "\n" +
            "eyr:2025 pid:322775528 hgt:155cm hcl:#efcc98 iyr:2015 byr:1996 ecl:oth\n" +
            "\n" +
            "byr:1960\n" +
            "hgt:183cm pid:764315947 eyr:2030\n" +
            "hcl:#ceb3a1 ecl:brn\n" +
            "\n" +
            "eyr:2029 hgt:168cm byr:1929 pid:800222003 ecl:gry hcl:#8f8aaa\n" +
            "iyr:2011\n" +
            "\n" +
            "hcl:#623a2f ecl:hzl hgt:168cm pid:795434985 eyr:2020 iyr:2020 cid:209\n" +
            "byr:1970\n" +
            "\n" +
            "cid:325\n" +
            "byr:2007 eyr:1933 hgt:188in\n" +
            "pid:713080083 ecl:#d624ca iyr:2030 hcl:z\n" +
            "\n" +
            "hcl:#7d3b0c pid:431742871\n" +
            "ecl:hzl hgt:169cm cid:340\n" +
            "eyr:2023\n" +
            "iyr:2017 byr:1994\n";

    static boolean validHeight(String text, String units, int lowerBound, int upperBound) {
        if (text.length() > 2 && text.endsWith(units)) {
            return validIntRange(text.substring(0, text.length() - 2), lowerBound, upperBound);
        }
        return false;
    }

    static boolean validIntRange(String text, int lowerBound, int upperBound) {
        try {
            int value = Integer.parseInt(text);
            return value >= lowerBound && value <= upperBound;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class LineSupplier implements Supplier<String> {
        final Scanner in;

        public LineSupplier(Scanner in) {
            this.in = in;
        }

        @Override
        public String get() {
            return in.hasNext()
                    ? in.nextLine()
                    : null;
        }
    }

    private static class BatchSupplier implements Supplier<String> {
        final Scanner in;

        public BatchSupplier(Scanner in) {
            this.in = in;
        }

        @Override
        public String get() {
            StringBuilder buffer = new StringBuilder();
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) {
                    break;
                }
                buffer.append(' ').append(line);
            }
            String res = buffer.toString();
            return res.isBlank() ? null : res.trim();
        }
    }
}
