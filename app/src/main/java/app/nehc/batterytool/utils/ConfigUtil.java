package app.nehc.batterytool.utils;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import app.nehc.batterytool.R;
import app.nehc.batterytool.bean.FuncItem;

public class ConfigUtil {
    private static String PACKAGE_NAME;
    private static String CONFIG_FILE_PATH;
    private static final String CONFIG_FILE_NAME = "setting_config.xml";

    /**
     * 初始化配置文件
     */
    public static void initSettingConfigFile(Context context) {
        try {
            BufferedReader br = null;
            File configFile = new File(CONFIG_FILE_PATH);
            if (!configFile.exists()) {
                br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.default_setting)));
                String tmp;
                StringBuilder builder = new StringBuilder();
                while ((tmp = br.readLine()) != null) {
                    builder.append(tmp);
                    builder.append('\n');
                }
                //开始写入文件
                context.openFileOutput(CONFIG_FILE_NAME, context.MODE_PRIVATE).write(builder.toString().getBytes());
            }
            try {
                //关闭流
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPackageName(String packageName) {
        PACKAGE_NAME = packageName;
        CONFIG_FILE_PATH = "/data/data/" + packageName + "/files/setting_config.xml";
    }

    /**
     * 解析setting-config.xml成FuncItem对象列表
     *
     * @return
     */
    public static List<FuncItem> getFuncItemList() {
        List<FuncItem> funcItemList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(CONFIG_FILE_PATH));
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                switch (nodeList.item(i).getNodeName()) {
                    case "setting-item":
                        FuncItem funcItem = new FuncItem();
                        Node node = nodeList.item(i);
                        funcItem.setFuncId(Integer.parseInt(node.getAttributes().item(0).getNodeValue()));
                        NodeList childNodes = node.getChildNodes();
                        //此for循环解析成单个FuncItem对象
                        for (int i1 = 0; i1 < childNodes.getLength(); i1++) {
                            Node cNode = childNodes.item(i1);
                            if (cNode.getNodeType() == Node.ELEMENT_NODE) {
                                switch (cNode.getNodeName()) {
                                    case "name":
                                        funcItem.setFuncName(cNode.getFirstChild().getNodeValue() + "");
                                        break;
                                    case "isSwitch":
                                        if (cNode.getFirstChild().getNodeValue().equals("1")) {
                                            funcItem.setSwitchItem(true);
                                        } else {
                                            funcItem.setSwitchItem(false);
                                        }
                                        break;
                                    case "isEnable":
                                        if (cNode.getFirstChild().getNodeValue().equals("1")) {
                                            funcItem.setEnable(true);
                                        } else {
                                            funcItem.setEnable(false);
                                        }
                                        break;
                                }
                            }
                        }
                        funcItemList.add(funcItem);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return funcItemList;
    }

    /**
     * 写入设置
     *
     * @param id
     * @param isEnable
     */
    public static void writeSetting(int id, boolean isEnable) {
        String isEnableToStr;
        if (isEnable)
            isEnableToStr = "1";
        else
            isEnableToStr = "0";
        //
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(CONFIG_FILE_PATH));
            Element element = document.getElementById("" + id);
            //设置开关状态
            element.getChildNodes().item(5).getFirstChild().setNodeValue(isEnableToStr);
            //保存至文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new DOMSource(document);
            Result result = new StreamResult(new File(CONFIG_FILE_PATH));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
