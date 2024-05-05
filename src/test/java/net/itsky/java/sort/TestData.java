package net.itsky.java.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class TestData {

    private static final Random rand = new Random();
    public static final List<String> UNSORTED = List.of("FL8YPS", "6DSNQQ", "U419AB", "X82QVN", "CU25H4", "ZZCRWY");
    static final List<String> SORTED = new ArrayList<>(UNSORTED);
    public static final List<String> LONG_UNSORTED = List.of("0H9Y41BL8WUQ", "7WPFZU00FHJQ", "D0OBE9SD6DPE", "AGBYG6MMCC9B", "E9SPYKWAITU9", "1VT6J5VSDMX9", "WSW1YJI40W8H", "378PPNCN6OW8", "JL819RD3CFTS", "C2YGBBGXXCBV", "KAGQZ7DY82WS", "C6N3W0RU4N5M", "XSJ9V3MN73TA", "8OPVKYG8JHYA", "UN0P5TKDEA05", "XP0KKYPUSTSF", "JS308V9PAJ2Z", "09NYKUUCTM5U", "AFID27K03CXA", "EPS16MESPLM7", "4FSSP0UJB6Q6", "FJG8FS1F2QXB", "L06RQWNPRFLJ", "TW9GAH01TK02", "2BSNA7IBHTRH", "H7IQKOMZ388X", "Y4B7SEEE5QMP", "C22P8R9UBPS7", "KVJE98AOILZ8", "LJLDM4AEA0AD", "1EVSCZGQ55MV", "E1LN8FRI5VNQ", "32NYE2DD9MBO", "OCKJPM3NKQ7P", "0IFOJ8OAY4H9", "5ERNL1PBOKOC", "76I2GY4KQOXP", "8EKLG4FQFU8K", "VP0J12B839EU", "94D3IKF9BITJ");
    static final List<String> LONG_SORTED = new ArrayList<>(LONG_UNSORTED);
    public static final List<String> NUMBERS_UNSORTED = IntStream.range(0, 1001).map(x -> (1 + x + x * x) % 1001).boxed().map(x -> String.format("%04d", x)).toList();
    static final List<String> NUMBERS_SORTED = new ArrayList<>(NUMBERS_UNSORTED);
    public static final List<String> NUMBERS_LONG_UNSORTED = LongStream.range(0, 1002003).map(x -> (1 + x + x * x + x * x * x) % 1002003004L).mapToObj(x -> String.format("%010d", x)).toList();
    static final List<String> NUMBERS_LONG_SORTED = new ArrayList<>(NUMBERS_LONG_UNSORTED);
    public static final List<String> NUMBERS_PREFIXED_UNSORTED = LongStream.range(0, 1002003).map(x -> (1 + x + x * x + x * x * x) % 1002003004L).mapToObj(x -> String.format("XYZ%010d", x)).toList();
    public static final List<String> NUMBERS_MIXED_PREFIXED_UNSORTED = NUMBERS_PREFIXED_UNSORTED.stream().flatMap(s -> Stream.of("XYZT" + s, "\ufedc\ufedb\ufeda\ufed9" + s)).toList();
    static final List<String> NUMBERS_MIXED_PREFIXED_SORTED = new ArrayList<>(NUMBERS_MIXED_PREFIXED_UNSORTED);
    static final List<String> NUMBERS_PREFIXED_SORTED = new ArrayList<>(NUMBERS_PREFIXED_UNSORTED);
    public static final List<String> ASIATIC_UNSORTED = List.of("A", "農林大臣", "", "B", "和田博雄", "", "C", "罗放", "", "D");
    static final List<String> ASIATIC_SORTED = new ArrayList<>(ASIATIC_UNSORTED);

    public static final List<String> EXTREMES = createList(3, 0x00, 0x01, 0x7f, 0x80, 0xff, 0x100, 0x1ff, 0x200, 0x3ff, 0x400, 0x401, 0x4ff, 0x500, 0x501, 0xfeff, 0xff00, 0xfff0, 0xfff1, 0xfffc, 0xfffd, 0xfffe, 0xffff);

    public static final List<String> EXTREMES_UNSORTED = EXTREMES.stream().map(s -> String.format("%8d %s", rand.nextInt(100_000_000), s)).sorted().map(s -> s.substring(9, s.length())).toList();
    public static final List<String> EXTREMES_SORTED = new ArrayList<>(EXTREMES_UNSORTED);

    public static final List<String> CYR_LAT_MIX = createList(3, 0x00, 0x01, 0x7f, 0x80, 0xff, 0x100, 0x1ff, 0x200, 0x3ff, 0x400, 0x401, 0x4ff, 0x500, 0x501);

    public static final List<String> UKRAINIAN_WORDS = List.of("ще", "Намісник", "Архіви/ЦДАВО/5069/1", "Сторінка:Історія", "q3xkwgo6496mbzxwqtomkqdlbro5yyn", "демократія«,", "все", "я", "самого", "вояк",
            "п'ятиріччі", "і", "ґанку", "наймички", "погоджувати", "закричала", "було", "собі", "України", "поляні", "коня", "перевірки.", "Держави", "мають", "він", "Чікаго:", "Дитячі", "не", "від",
            "на", "в", "—", "різьбарі,", "втікає", "перед", "нова", "нам", "Словник", "Гурский", "якого", "352868", "хто", "української", "саду,", "text/x-wiki", "Вертатись:", "з", "віддихав",
            "wikitext", "В.А.", "прилягають", "нашого", "Leh", "і", "скрикнула:", "спочиваючи.", "згадують", "не", "Мукачевому", "f45d891p6hb6bnty909gxw24fh2k9ky", "склався", "гуде",
            "3392h1sw1pn2ozeir63oc5hi7gkpkuu", "2021-02-19T14:02:25Z", "і", "не", "чрез", "засіяла\".", "захоплювався", "як", "з", "за", "2022-12-10T12:02:16Z", "Кобизщиенъко", "7610",
            "Сторінка:Повне", "Всеукраїнської", "репрезентував", "щось", "5381", "на", "Крови —", "оскільки", "2022-11-06T22:17:42Z", "що", "запросити", "коли", "і", "що", "та", "Мені",
            "2017-06-23T16:40:16Z", "ся", "руками…", "виплакалаб", "поганий", "301576", "робити?", "в", "з", "LehBot", "387262", "і", "Зараз", "вже", "внесення", "—", "аудитом", "Arxivist",
            "сільради —", "до", "Праведник", "і", "рішення", "филь,", "Заснула", "до", "неудачах", "сказано,", "одним", "Madvin", "Придінцевим", "М", "дані", "від", "довго", "не", "11101",
            "найбільше —", "(1937)/голодовий", "з", "що", "несимпатичний", "дорослого,", "більше,", "proofread-page", "світ", "189354", "на", "по", "Сторінка:Максимович", "історичному)", "й", "217",
            "Tvory.", "135704", "только", "Т.", "був", "не", "Мама…", "щирий,", "„Український", "508774", "8632", "116", "Або", "навзрид.", "такъ", "Забирайтеся,", "підлогу,", "киплять", "звернути",
            "Відраднівську,", "Том", "42241", "посадок", "Бендерах.", "77709", "f7so84tscq945xw9owrl8jgrcvt348i", "(статті", "неї", "так, —", "Fahrenheit", "тільки", "обов'язкових", "корпусу",
            "Біографічно-літературний", "ти", "Palych", "мови", "і", "невірною,", "*", "чоловік,", "самостійною", "то", "—", "nf92ux6q2v30gydxb14c38p09btnvjm", "text/x-wiki", "панську", "та", "“Ой,",
            "зобов'язані", "направду", "Гонсецкий?", "0", "Всі", "text/x-wiki", "та", "LehBot", "це", "я", "бы", "Словарь", "з", "на", "так", "Нараз", "11101", "і", "піде", "од", "від", "Вадим:",
            "гикав", "250", "264813", "підкоп,", "що", "і", "2021-08-27T10:04:17Z", "робочої", "очима", "долу", "Почав", "250", "тріснув", "самому", "не", "text/x-wiki", "графів", "А.", "сідло,",
            "М.", "я", "нова", "§§ 10", "(tu,", "березах!", "віддала", "сам", "полици,", "з", "нова", "І", "(1212,", "родини,", "Потебня,", "на", "263130", "мовиться", "майже", "від", "цього",
            "(1924)/повідслужуватися", "величні", "ховав.", "„Не", "48727", "306951", "тепер", "гнеть", "немов", "його", "ряди", "міській", "150;", "181332", "та", "ся", ".", "мовчала.", "114994",
            "палуби,", "задивився,", "Palych", "один", "Leh", "ВУЦВК", "Ю.", "тяжке", "text/x-wiki", "0", "text/x-wiki", "ся,", "0", "на", "видатків", "(1937)/газдувати", "що", "клопотів",
            "людности.", "допомогою", "„Санчо! —", "250", "538363", "и", "кримінальну", "2023-12-19T22:00:45Z", "й", "12,", "органами", "жили,", "кулеметів", "по", "*", "Архів:ДАКО/Р-5634/1/2914",
            "і", "хоч", "надра.", "вся", "влади", "адміністрація", "„А", "віддано", "Palych", "нею,", "хмари", "їхати", "ці", "Любо́ви", "Ніночко,", "С.", "щасливо", "(1924)/ростік", "всїї", "не",
            "або", "1980", "wikitext", "Scribunto", "студници,", "text/x-wiki", "встала.", "сусіда-панок,", "можливости", "LehBot", "—", "знову", "й", "люби", "531897", "собі", "Balakun", "в", "1918",
            "Се", "для", "text/x-wiki", "Індія", "Правительство", "з", "625870", "1905.pdf/96", "131612", "єго", "нещасний,", "0", "відкрито", "а", "Словарь", "і", "Словарь", "який", "Pywikibot",
            "та", "поставив,", ":3-1)", "це", "тут —", "„Гоіться…", "тут", "а", "иньші", "не", "244597", "чи", "„Юліяно,", "116", "самого", "одна", "1891).", "Madvin", "якому", "тим", "ярками", "з",
            "нова", "text/x-wiki", "гірше", "Вода", "дає", "думку", "Марка", "Економіка.", "вовчику-братіку,", "Пожаром", "text/x-wiki", "там", "1927.pdf/361", "самоврядування", "наш", "д.",
            "Словарь", "саму", "між", "Архів:ДАКО/280/166/9194", "літала", "нахмарите", "землю", "2021-08-26T22:51:10Z", "на", "цього", "приром,", "198496", "Полтавка", "11101", "11101", "і",
            "Богдан.", "ударив", "справ", "Тутъ", "в", "redirect", "Радою", "грубим", "Боґу,", "від", "Madvin", "(владѣетъ)", "і", "активну", "розпоряджень", "уряду", "Приїзджайте", "випадку",
            "чорний", "щоб", "належала", "Чи", "proofread-page", "темного", "ангели", "своєї", "теж", "почувся", "Про", "житла.", "11101", "свого", "що", "—", "коло", "він", "Іван", "далі",
            "2020-06-29T20:16:13Z", "приїхав", "за", "be", "ті-ж", "Крилатий", "Антон", "розтрата", "країни,", "люди,", ".", "під", "LehBot", "виборів", "району.", "text/x-wiki", "Ніхто", "з’іскати,",
            "вуйку,", "як", "„Червона", "з", "певний;", "11101", "перуном", "32668", "ненормального", "ти", "її", "хору.", "1–3).", "давно", "-", "не", "сватові", "ним", "вельми", "знечевя",
            "Громадяни", "'''Stаtkiewicz", "0", "сантиметрів.", "ці", "як", "Сторінка:Майк", "близько, —", "Чмир", "услуги?", "полізло", "світлі,", "що", "Palych", "подумаєш.", "утримуваних", "Чи",
            "фортецю", "Сонце", "4488", "коло", "уста. —", "проти", "Папагено,", "0", "не", "коло", "і", "text/x-wiki", "мови", "І", "оддихајучи;", "на", "12.7.1944", "Центр", "подиктую.", "на",
            "text/x-wiki", "(1961).djvu/28", "казавъ", "-", "11101", "УСРР", "жваву", "Державного", "досить", "Так!", "не", "його", "покрученого", "не", "Зітхаючи", "Франції.", "Arxivist",
            "перечисляющагося", "собою", "wikitext", "й", "поляки", "собою", "волосся", "шматом", "—", "wikitext", "352026", "1930).djvu/7", "та", "карти", "купання —", "краю", "проголосити", "мови",
            "Бувайте", "що", "ca.і", "Нині", "Галицьких", "LehBot", "Тустановичах", "зв'язки", "хвилі", "Ще", "у", "небезпеки,", "Створено", "ніч.", "nhfid4pcb58ky09o8bjyqtsuqa6meai", "стежили",
            "2021-02-19T14:47:43Z", "передвечірній", "text/x-wiki", "Створено", "років,", "на", "йому", "зоології", "держава.", "мене", "стольного", "ніби", "кількох", "неписьменності,", "Як", "Якщо",
            "вас.", "скрикнув", "її", "Європи", "text/x-wiki", "250", "wikitext", "зн.", "0", "їх", "wikitext", "та", "2020-06-28T10:31:16Z", "відділів.", "Проць,", "293443", "174226", "може",
            "410850", "Г.С.", "1927.pdf/5", "були", "бути", "Кобилянська.", "смерть.", "братства", "text/x-wiki", "спочатку", "ініціятива", "подзвонюйте,", "хто", "чисто", "зворохобив", "добробут",
            "Україну.", "'''Стаття", "темнотою", "ладити", "tq6rcb3y4j7z0b1z7kzaw5nthpa8o9j", "Спільна", "Мовчанка.", "вжити", "гиленьку", "116", "text/x-wiki", "2022-10-25T21:50:31Z", "wikitext",
            "mbvm09fffcspf2yqx522oezr4ssfjkc", "text/x-wiki", "і", "холодїти", "Української", "217", "вже", "хутрового", "тебе,", "ж", "епископ", "блистіли", "истории", "А", "сторінку",
            "2021-02-19T12:36:18Z", "ekd40nxc55s4bcb48r959350dk4zbez", "й", "Архів:ДАКО/Р-5634/1/1443", "Угорської", "їх", "мови", "сльози", "знизу,", "уважним,", "2021-02-19T12:17:57Z", "день",
            "11101", "180663", "цієї", "0", "text/x-wiki", "Palych", "роботи.", "15", "„Давайте,", "Союзу", "зміни", "стрясає", "Та", "Він", "її", "341107", "організації", "2021-03-28T08:59:55Z",
            "Balakun", "Leh", "курей,", "мови", "0", "Игореві.", "недоторканність", "суспільств/4", "Оддиш", "Й", "217643", "Монографії", "фронтиру", "800", "стояла", ":Та", "Том", "діалогів",
            "331814", "250", "мій", "Leh", "може", "porwał", "тепер", "старою", "тобою!", "fjpdrhfs1kuxyf3laq5k8walgmut0a2", "102", "колгоспів", "страшно", "0", "Де", "повстанської", "Madvin",
            "погнали", "до", "доповнив", "повноцінного", "не", "на", "міг:", "та", "Сторінка:Срезневский", "на", "Список", "окремих", "збирати", "за", "несли", "відповідному", "мови", "знають", "Сб.",
            "і", "за", "на", "1kzs3z57lmr4iq37nxywxkwdxr57cki", "а", "мій", "поставили", "Palych", "в", "них", "себе", "Гайдамаки.", "питання,", "й", "Архіви/ЦДАВО/47", "250", "зрештою,", "--",
            "сон!", "—", "користь", "0", "одцурався.-", "вас", "сфері", "він", "місці,", "Кубанського", "вистрілявши", "«Souvenir»…", "знов", "мови", "Краків", "—", "бачили", "обдерти", "розкіш,",
            "Palych", "міг", "в", "Є.", "236340", "прецінь", "цілісності", "wikitext", "крутими", "213889", "text/x-wiki", "кордоном.", "wikitext", "р.", "ніхто", "5149", "умертвіи", "LehBot",
            "шукати", "edit", "Твоїм", "желонку", "л", "до", "момент", "хахол! —", "хоть", "режиму", "сам", "а", "мови", "Щоб", "чи", "на", "Круки,", "3aok2scc829g59a1ph1wvsfmv9jnx3l", "26.VI.1918",
            "роздразнено", "думці", "Південно-Західні", "Найсильніші", "(1924)/прийняти", "Паста", "147an96sszhqidzp1fs4l8rtz6owrby", "—", "беззвучно", "з", "але", "Иванова;", "і", "text/x-wiki",
            "Балабушевич", "Тайкури", "усіх", "та", "dlck32pvvbpvcbskz58m1eprdf0pd67", "сказав,", "Пета.", "—", "Сторінка:1.", "до", "11101", "М.", "Обставини", "раз-у-раз", "Багато", "244494",
            "вага", "11833", "провадять", "мене", "зелені", "з", "Сторінка:Микола", "0", "116", "198498", "на", "на", "признався.—", "М.,", "показала", "Вишниевский;", "5381", "Сторінка:Джек",
            "шланга", "праці", "хреста", "не", "(Львів,", "2021-04-21T22:23:47Z", "розвитку", "радиј", "і", "Я", "щиро", "походом", "217", "з.", "чуттів", "кидали", "сів", "категорії", "V.",
            "Нікчемна", "України", "0w1brrzu4usz3ua2eidyvm4u1sbccy1", "звичайно", "Б.", "Balakun", "кількість", "Підкарпатської", "0", "виявляв", "wikitext", "Архів:РДАДА/294/2",
            "qg9ioj8osh0j9m77jdaysul4imk6nor", "задивлювалися", "час", "в", "хоче", "5381", "proofread-page", "на", "воскреснемо,", "—", "Сторінка:Михайло", "брати", "кілька", "поблагословлю!",
            "повіренний", "Leh", "України", "се", "похмуро", "її", "а", "Владимиров,", "Жандарм.", "281739", "пухир,", "116", "таке!", "251074", "підставі", "мови", "нам", "само", "to", "голубі",
            "іспанського", "що", "І", "не", "2021-10-23T15:41:52Z", "звязана,", "Palych", "до", "remove", "wikitext", "text/x-wiki", "вдосконалення", "Левковичі", "s5u5y87s4td131jrp4v6we91fvk03dl",
            "Словник", "гніту,", "авторським", "так,", "И", "встиг", "троянду", "2.", "Якби", "теличку,", "Костомаров.", "після", "і", "малювати", "Випуск", "1994;", "неможливої", "високо", "від",
            "1879.pdf/5", "що", "завдяки", "Голову", "хотїв", "2d26lykpm7su35keuqlb2on0szq4llb", "постанови", "брати", "dymxv1quljza82pn186lji6ob9ufr3t", "що", "тощо,", "спонукало", "Leh", "в", "як",
            "непокій.", "свобідний", "За", "журнальних", "органи", "й", "2022-08-24T15:16:21Z", "11101", "Вода", "зробити", "же", "і", "83245", "wikitext", "кахала.", "зерно", "сяючих", "очі.",
            "такихъ");

    public static final List<List<String>> LIST_OF_CLASS_MIN_MAX = List.of(

            List.of("0", "0zzyhmo8hgb7zdcw05n0no6i0knaeuk "),
            List.of("0", "0. г. (Фізично-математичні науки, Київ). Данілевський В. Я."),
            List.of("10–12", "115xex11ped0srlf2kiedmauk4ios4w "),
            List.of("11", "11–16 листопада 2002 р. у м. Марселі (Франція) відбувся традиційний форум архівістів — XXXVI Міжнародна конференція Круглого столу архівів (CITRA), у рамках якого було проведено Генеральну асамблею Європейського відділення (EURBICA) Міжнародної ради архівів та  Делегатські збори МРА."),
            List.of("1", "1⅓ пап. арк. 1 пап. арк. 62.500 літ."),
            List.of("1", "1ң іко-географічного, ентомологічного. З метою виявлення придатности"),
            List.of("20", "20—22 III. 1904 р."),
            List.of("20", "202999 "),
            List.of("2", "2—3) Шкребачки з пластини, ретушованого оброблення (3)."),
            List.of("2", "2і5 БеРед очима мені ця маєтність багата, даремно"),
            List.of("24–25 лютого 2011 р. у конференц-залі комплексу споруд центральних державних архівів України (м. Київ, вул. Солом'янська, 24) відбулося розширене засідання колегії Державного комітету архівів України, присвячене підбиттю підсумків роботи державних архівних установ у 2010 р. та завданням на 2011 р.", "259x675o2z3s8hpp7mmao5wuvnm3no3 "),
            List.of("250", "250. Уляницкий;"),
            List.of("25", "25–28 червня 2008 проводив свою роботу у Києві Сьомий конгрес україністів. У програмі заявлено було понад 300 доповідей і виступів вчених з 18 країн. На 10 секціях було представлено напрями українознавчих студій: історія України, політологія, етнокультурологія, філософія, релігієзнавство, мистецтвознавство, мовознавство, літературознавство, фольклористика."),
            List.of("3", "3’їзд Рад відкрився ще до кінця конференції. В цей день у Києві було надто неспокійно. Залізницею, Дніпром та кіньми стягалися до Центральної Ради делегати з'їзду. Там їх приймали, накачували, частували і, давши від себе проводарів, посилали їх до мандатної комісії з'їзду."),
            List.of("3", "3ї місто славне та багате роскинуло ся, люду торговель-"),
            List.of("4", "4—5 см нижче від вiнця; самий вінчик посуду теж мережаний"),
            List.of("4", "4» дивуватві"),
            List.of("53", "53–60 Гостомель"),
            List.of("5381 ", "538199 "),
            List.of("53   Соколов", "539ztx4liu7nppd185of2xojxzilfow "),
            List.of("5", "5—в; 73—е; 123—ч; 124; 128—з; 134; 135; 189—а, б; 212—в; 263-А, 9. (Перекладъ святого письма нового завіту — П. Куліша)."),
            List.of("5", "5 лютого, після операції в районі Канева обидві дивізії, скупчивши кінноту під керуванням отамана Тютюнника, переправилися в район Сміла–Бобринська. Піхотою й обозом керував отаман Загродський. Піші частини дивізії через Руську-Поляну (повз Черкаси) були перекинуті в село Вергуни. Під час походу був такий мороз, що було чимало обморожених, а один козак замерз."),
            List.of("6", "6—9 січня 1883."),
            List.of("6", "6» стеж"),
            List.of("Lecz duch z wyżyn cię okalał,", "Lexusuns "),
            List.of("Le doglianze del Casimiro fatte al suo fratello per esser stato il primo a notificar all' Imperatore con espressa persona la morte del Re et alli Magnati del Regno insinuandoseli per la corona la risposta, che ha dato alla Regina a nome di S. A. in presenza del confessore del Casimiro sopra le dette doglianze fatteli da S. M. per la unione e buona corrispondenza fraterna, cioè di non hauer errato S. A. a dar parte della morte del Re, a chi doueva, mentre il fratello si trouaua assente.", "Lexusuns "),
            List.of("L", "L’amour, pour tant de cœurs l’objet de plus doux charmes"),
            List.of("L", "LХХХІХ"),
            List.of("pr00t3kcea2vs2zor6popbnwwcxtwh3 ", "przy jw. imć panu Kasztelanie Krakowskim dobrych i ochotniego z wojska niemało, do tego armat i preparamentu wojennego było dosyć, dodali im pomocy ci, którzy byli principaliora capita u nich, jakoto imć pana Czarneckiego półkownika, jw. imć pana Stanisława z Wisznica Lubomirskiego wojewodę krakowskiego, a temu był regiment wcale i zupełnie powierzony, przy nim imć pana Brzuchańskiego białocerkiewskiego, pana Woniłowicza kaniowskiego pułkowników. Zdrajca Chmielnicki uczęstował zaraz posłów łańcuchami i okowami, tandem owi już pozostali sine integro consilio w ostatku uchodzić poczęli z tego miejsca taborem, w której drodze siła ludzi stracili. Tandem znowu pod Kniaziemi Bajerakami osadzeni nadzieję już straciwszy de salute sua i przez niedziel dwie"),
            List.of("pr6188ittjxuns2gd3mtan81euhat7e ", "pryun6km6bqbykm2m5lq9rcnjpwtv19 "),
            List.of("proofread-index ", "proofread-page "),
            List.of("p. було в гімназії 477, а на філософії 174 учні.", "półprawdy, z uwagi przede wszystkim na zawężenie współczesnego kontekstu politycznego,"),
            List.of("p. було в гімназії 477, а на філософії 174 учні.", "pі(mпs)"),
            List.of("te04p9ofw2uhzx6mk55ll1u43eqnh0b ", "tezę, że powyższe motyw pobudzane są w przypadku stosunku Moskwy do Polski swoistym"),
            List.of("te506fmqv3q7isrus149hd41lf1ln11 ", "texz26lrlxq7pj2bp13um5d8kbyrs1u "),
            List.of("text above picture", "texz26lrlxq7pj2bp13um5d8kbyrs1u "),
            List.of("t", "tätiger Massen des groBen zаristischen Imperiums brachte"),
            List.of("t", "tідкреслив, що Союз дивиться на Україну, як на майбутню самостійну незалджну"),
            List.of("w Amszone?", "wакуль."),
            List.of("wicklung des ukrainischen Volkеѕ. Der Aufbau der ukrai-", "wikitext "),
            List.of("wicklung des ukrainischen Volkеѕ. Der Aufbau der ukrai-", "within \"source\".  Indices are 1-based.  If \"target\" is not found, then this "),
            List.of("wiki ", "wikitext "),
            List.of("А", "А… той скарб… ти де подів?“"),
            List.of("—", "― Алилуя заспіваймо,"),
            List.of("—", "—■ Гав!"),
            List.of("С    S    ", "Сѣє вѣтер, вѣтром жити буде."),
            List.of("СсА що ти обіщавъ за дівчинуЪ . . . Гримнувъ Ба- ", "Стїна, м.; XV: 70, 71."),
            List.of("С", "Сꙋдьба ѹсѣла на поранно̂й зари"),
            List.of("Стиборівка ", "Стїна, м.; XV: 70, 71."),
            List.of("Стор", "Стоючи на ньому, рознощики, з високо піднятими в гору, ще мокрими від друкарської фарби задрукованими листками ґазети, почали вигукувати своїм звичаєм:"),
            List.of("—                              Так і далі буде...— думала вона.— Так!.. Ой, мамо! Нащо ви покинули ", "—ь"),
            List.of("לערי ריינהארט ", "﻿ "));

    static final List<String> UKRAINIAN_WORDS_SORTED = new ArrayList(UKRAINIAN_WORDS);

    static {
        Collections.sort(SORTED);
        Collections.sort(LONG_SORTED);
        Collections.sort(NUMBERS_SORTED);
        Collections.sort(NUMBERS_LONG_SORTED);
        Collections.sort(NUMBERS_PREFIXED_SORTED);
        Collections.sort(NUMBERS_MIXED_PREFIXED_SORTED);
        Collections.sort(ASIATIC_SORTED);
        Collections.sort(EXTREMES_SORTED);
        Collections.sort(UKRAINIAN_WORDS_SORTED);

    }

    static List<String> createList(int length, int... letters) {
        List<String> result = new ArrayList<>();
        int m = letters.length;
        for (int n = 0; n <= length; n++) {
            int[] idx = new int[n];
            while (true) {
                String str = IntStream.range(0, n).map(i -> idx[i]).map(j -> letters[j]).mapToObj(l -> String.valueOf((char) l)).reduce("", (s, c) -> s + c);
                result.add(str);
                boolean done = true;
                for (int i = 0; i < n; i++) {
                    idx[i]++;
                    if (idx[i] < m) {
                        done = false;
                        break;
                    }
                    idx[i] = 0;
                }
                if (done) {
                    break;
                }
            }
        }
        return result;
    }
}

