package kevin.utils.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 描述: 生成编码
 *
 * @author liujicheng
 * @date 2020/9/19
 */
public class GenerateCode {

    static final String filePath = "E:\\test.txt";

    // 传入参数加上这个数，可视为 起始数
    private static long miniIndex = 5555550;
    //code 长度 依次匹配，超过了再进行下一个匹配
    private static int codeLen[] = {9, 12, 15};
    // 下标为真实数值 对应实际显示的字符
    private static char[] charArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    //    private static char[] charArr = {'0','1','2','3','4','5','6','7','8','9','A','B','E','F','H','J','K','M','N','P','R','S','T','W','X','Y'};
    //补位的字符 默认是取 charArr 的第一个，这样不会重复 因为变换模式就是默认取这个数
    private static char blackChar = charArr[0];
    private static Map<Character, List<List<Integer>>> modamap = new HashMap<>();

    static {
        // 下标为右起第几 ，数值为 放置原来code对应的下标  / 外层数组分别和 codeLen 里面的值对应， 这里取 9,12,15 三种长度
//        modamap.put('0', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('1', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('2', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('3', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('4', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('5', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('6', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('7', Arrays.asList(0, 4, 2, 3, 1, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('8', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
//        modamap.put('9', Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14));


        modamap.put('0', Arrays.asList(
                            Arrays.asList(0, 3, 1, 2, 5, 8, 4, 7, 6),
                            Arrays.asList(0, 9, 4, 2, 6, 3, 10, 1, 8, 7, 11, 5),
                            Arrays.asList(0, 3, 6, 7, 8, 2, 14, 10, 1, 4, 13, 5, 11, 12, 9)
        ));
        modamap.put('1', Arrays.asList(
                            Arrays.asList(0, 8, 4, 6, 5, 1, 2, 3, 7),
                            Arrays.asList(0, 2, 1, 4, 9, 5, 11, 3, 6, 8, 7, 10),
                            Arrays.asList(0, 9, 8, 2, 1, 11, 13, 14, 4, 12, 10, 5, 3, 6, 7)
        ));
        modamap.put('2', Arrays.asList(
                            Arrays.asList(0, 5, 7, 1, 6, 3, 8, 2, 4),
                            Arrays.asList(0, 9, 8, 3, 7, 10, 5, 2, 4, 11, 6, 1),
                            Arrays.asList(0, 8, 12, 14, 10, 4, 9, 7, 3, 6, 5, 1, 11, 13, 2)
        ));
        modamap.put('3', Arrays.asList(
                            Arrays.asList(0, 8, 3, 2, 4, 6, 7, 1, 5),
                            Arrays.asList(0, 9, 4, 6, 11, 1, 5, 10, 8, 7, 2, 3),
                            Arrays.asList(0, 7, 12, 2, 3, 9, 4, 13, 10, 6, 8, 11, 5, 1, 14)
        ));
        modamap.put('4', Arrays.asList(
                            Arrays.asList(0, 5, 6, 2, 3, 8, 4, 7, 1),
                            Arrays.asList(0, 4, 3, 6, 9, 2, 7, 8, 11, 5, 10, 1),
                            Arrays.asList(0, 12, 4, 1, 2, 11, 9, 8, 3, 13, 6, 7, 10, 14, 5)
        ));
        modamap.put('5', Arrays.asList(
                            Arrays.asList(0, 7, 4, 8, 6, 3, 5, 2, 1),
                            Arrays.asList(0, 9, 2, 10, 8, 3, 11, 1, 4, 5, 7, 6),
                            Arrays.asList(0, 3, 14, 7, 9, 1, 10, 5, 11, 12, 4, 2, 13, 6, 8)
        ));
        modamap.put('6', Arrays.asList(
                            Arrays.asList(0, 3, 1, 7, 6, 8, 2, 4, 5),
                            Arrays.asList(0, 4, 11, 8, 7, 6, 9, 2, 5, 3, 1, 10),
                            Arrays.asList(0, 1, 14, 12, 9, 13, 8, 11, 7, 2, 3, 6, 10, 5, 4)
        ));
        modamap.put('7', Arrays.asList(
                            Arrays.asList(0, 4, 2, 5, 8, 6, 7, 3, 1),
                            Arrays.asList(0, 8, 1, 7, 2, 4, 11, 3, 6, 10, 5, 9),
                            Arrays.asList(0, 4, 7, 10, 11, 3, 6, 1, 9, 5, 8, 2, 14, 13, 12)
        ));
        modamap.put('8', Arrays.asList(
                            Arrays.asList(0, 1, 2, 5, 6, 4, 7, 8, 3),
                            Arrays.asList(0, 1, 9, 2, 10, 8, 4, 3, 5, 11, 6, 7),
                            Arrays.asList(0, 13, 3, 12, 2, 1, 10, 8, 7, 14, 6, 5, 9, 4, 11)
        ));
        modamap.put('9', Arrays.asList(
                            Arrays.asList(0, 5, 3, 6, 8, 4, 7, 1, 2),
                            Arrays.asList(0, 5, 8, 9, 1, 11, 4, 2, 3, 6, 7, 10),
                            Arrays.asList(0, 4, 5, 9, 10, 8, 6, 1, 7, 12, 3, 13, 14, 11, 2)
        ));
    }

    public List<String> generateCodeList(long startIndex, long endIndex) {
        return null;
    }

    public String getCodeByNumber(long number) {

        return null;
    }

    // 根据原 code 第一位(右一) 采用不同的 显示方式
    public static String changeBitIndexByFirstBit(String code) {
        StringBuilder sb = new StringBuilder(code);
        char[] oldChars = sb.reverse().toString().toCharArray();
        List<List<Integer>> list = modamap.get(oldChars[0]);
        if (null == list) {
            throw new RuntimeException("没有找到对应变数 的位模式!");
        }
//        if (code.length()>list.size()) {
//            throw new RuntimeException("位模式变换错误,code 长度大于位模式模板");
//        }
        char[] result = new char[code.length()];
        List<Integer> suitModList = null;// 匹配的模式
        for (int j = 0; j < list.size(); j++) {
            if (code.length() <= list.get(j).size()) {
                suitModList = list.get(j);
                break;
            }
        }

        if (null == suitModList) {
            throw new RuntimeException("没有匹配到合适的位模式");
        }

        for (int i = 0; i < code.length(); i++) {
            Integer oldIndex = suitModList.get(i);
            result[i] = oldChars[oldIndex];
        }
        StringBuilder sb2 = new StringBuilder(new String(result));
        return sb2.reverse().toString();
    }

    //根据可用的字符数 将十进制数 调整为 可用字符表示的 字符串
    public static String getCodeByCharArrLen(long number) {
        number += miniIndex;
        int perBit = charArr.length;//每一位表示的数
        StringBuilder result = new StringBuilder("");
        long thisBitMax = perBit;//当前位最大数
        long thisBitNumber = new Double(Math.pow(perBit, 1)).longValue();
        while (number > 0) {
            long mod = number % thisBitNumber;
            result.append(charArr[(int) mod]);
            number -= mod;
            number /= thisBitNumber;
        }
        return result.reverse().toString();
    }

    // code 不够长度的，左边补上 0 或者其他占位符号
    public static String completeLenthOfCode(String code) {
        StringBuilder sb = new StringBuilder(code);
        for (int i = 0; i < codeLen.length; i++) {
            if (code.length() <= codeLen[i]) {
                for (int j = 0; j < codeLen[i] - code.length(); j++) {
                    sb.insert(0, blackChar);
                }
                return sb.toString();
            }
        }
        throw new RuntimeException("生成编码 已经超长  编码可能已经用尽");
    }


    public static void show(List<String> list) {
        System.out.println("total: " + list.size());
        try {
            FileUtils.write(new File(filePath), "", false);
            for (String str : list) {
                FileUtils.write(new File(filePath), str + ",", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取随机的 位模式字符串
    public static String generateNumberModStr(int min,int max) {
        Random random = new Random();
        String[] arr = new String[max-min];
        for (int i = 0; i < max-min; i++) {
            arr[i] = String.valueOf(min + i);
        }

        for (int i = 0; i < arr.length; i++) {
            int randomIndex = random.nextInt(arr.length -i);
            //抽号排到最后去
            String temp = arr[randomIndex];
            arr[randomIndex] = arr[arr.length -i - 1];
            arr[arr.length -i - 1] = temp;
        }
        return StringUtils.join(arr, ',');
    }

    public static String generate(Long number) {
        String str = getCodeByCharArrLen(number);
        str = completeLenthOfCode(str);
        str = changeBitIndexByFirstBit(str);
        return str;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();


        Set<String> resultSet = new HashSet<>();
        for (int i = 0; i < 9999999; i++) {
            if (0==i%10000) {
                System.out.println("已经产生数 : " + i);
            }
            String content = generate(Long.valueOf(i));
            if (!resultSet.contains(content)) {
                resultSet.add(content);
            } else {
//                System.out.println("产生了重复code : "+content);
                throw new RuntimeException("产生了重复code : "+content);
            }
        }


//        show(resultSet.stream().collect(Collectors.toList()));

        //生成模式数组
//        for (int i = 0; i < 10; i++) {
//            System.out.println(generateNumberModStr(1, 12));
//        }

        System.out.println("################   编码产生完毕:  " + (System.currentTimeMillis() - start));
    }




}
