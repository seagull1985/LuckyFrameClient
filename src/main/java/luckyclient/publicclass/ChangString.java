package luckyclient.publicclass;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 */
public class ChangString {

	/**
	 * �滻�����е��ַ�
	 * @param str
	 * @param variable
	 * @param changname
	 * @return
	 */
    public static String changparams(String str, Map<String, String> variable, String changname) {
        try {
            if (null == str) {
                return null;
            }
            str = str.replace("&quot;", "\"");
            str = str.replace("&#39;", "\'");
            //@@����ע��@����������
            int varcount = counter(str, "@") - counter(str, "@@") * 2;

            //������ڴ��Σ����д���
            if (varcount > 0) {
                luckyclient.publicclass.LogUtil.APP.info("��" + changname + "��" + str + "�����ҵ�" + varcount + "�����滻����");
                int changcount = 0;
                //�Ӳ����б��в���ƥ�����
                for (Map.Entry<String, String> entry : variable.entrySet()) {
                    if (str.contains("@" + entry.getKey())) {
                        if (str.contains("@@" + entry.getKey())) {
                            str = str.replace("@@" + entry.getKey(), "////CHANG////");
                        }
                        //�����滻�ַ����д���\"����\'�ᵼ��\��ʧ������
                        //entry.setValue(entry.getValue().replaceAll("\\\\\"", "\\&quot;"));
                        //entry.setValue(entry.getValue().replaceAll("\\\\\'", "\\\\&#39;"));
                        int viewcount = counter(str, "@" + entry.getKey());
                        str = str.replace("@" + entry.getKey(), entry.getValue());
                        luckyclient.publicclass.LogUtil.APP.info("��" + changname + "���ñ�����@" + entry.getKey() + "���滻��ֵ��" + entry.getValue() + "��");
                        str = str.replace("////CHANG////", "@@" + entry.getKey());
                        changcount = changcount + viewcount;
                    }
                }

                if (varcount != changcount) {
                    luckyclient.publicclass.LogUtil.APP.error(changname + "�����ñ���δ�ڲ��������ҵ������飡��������" + str + "��");
                }
            }
            str = str.replace("@@", "@");
            //�����ָ��ַ����д���\"����\'�ᵼ��\��ʧ������
            //str = str.replaceAll("\\&quot;", "\\\\\"");
            //str = str.replaceAll("\\&#39;", "\\\\\'");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * ͳ���ַ�
     * @param str1
     * @param str2
     * @return
     */
    public static int counter(String str1, String str2) {
        int total = 0;
        for (String tmp = str1; tmp != null && tmp.length() >= str2.length(); ) {
            if (tmp.indexOf(str2) == 0) {
                total++;
                tmp = tmp.substring(str2.length());
            } else {
                tmp = tmp.substring(1);
            }
        }
        return total;
    }

    /**
     * �ж��Ƿ�������
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * �ж��Ƿ�������
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static void main(String[] args) {

    }

}
