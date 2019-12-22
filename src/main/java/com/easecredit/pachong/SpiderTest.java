package com.easecredit.pachong;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: GaoXing
 * @Description
 * @data: Created by GaoXing on 2019/3/19.
 * @Modified By;
 */
public class SpiderTest {
    private static Logger logger = LoggerFactory.getLogger(SpiderTest.class);
    private final static String START_URL = "http://hd.chinatax.gov.cn/xxk/action/ListXinxikucomXml.do?dotype=casetime&id=@YEAR@年@MONTH@月";
    private final static String SECOND_URL = "http://hd.chinatax.gov.cn/xxk/action/GetArticleView1.do?op=xxkweb&id=@ITEMID@";
    private final static String AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
    private final static Long SLEEP_TIME = 3*1000l;
    private static List<JSONObject> resultList = new ArrayList<>();

    public static void main(String[] args){

        attentionSourceTest();
    }

    public static void attentionSourceTest(){
        for (int year = 2018 ; year < 2019 ; year++)
            for (int month = 4; month < 13; month++) {
                String url_1 = START_URL.replaceAll("@YEAR@", String.valueOf(year)).replaceAll("@MONTH@", String.valueOf(month));
                Document response = getResponse(url_1, AGENT,"");
                if (response != null) {
                    Elements items = response.getElementsByTag("root").get(0).getElementsByTag("item");
                    for (Element item : items) {
                        String itemId = item.attr("id");
                        String url_2 = SECOND_URL.replaceAll("@ITEMID@", itemId);
                        Document table = getResponse(url_2, AGENT,"");
                        if (table != null) {
                            handler(table);
                            if (resultList.size() > 10){
                                //在这里做数据的存储，愿意存表还是写文件还是怎么着都行。
                                resultList.clear();
                            }
                        } else {
                            System.out.println("请求失败,请求url为:" + url_2);
                        }
                    }
                } else {
                    System.out.println("请求失败,请求url为:" + url_1);
                }
            }
    }

    public static void handler(Document document){
        Element table = document.getElementsByTag("table").get(0);
        Elements trs = table.select("tr");
        JSONObject jsonObject = new JSONObject();
        for (Element tr : trs){
            Elements tds = tr.select("td");
            if(tds.size() == 2){
                String key = tds.get(0).text();
                if ("姓名".equals(key)){
                    return;
                }
                String value = tds.get(0).text() == null ? "" : tds.get(0).text();
                if (key.contains("纳税人名称")){
                    jsonObject.put("taxpayer",value);
                }else if (key.contains("纳税人识别号")){
                    jsonObject.put("taxNumber",value);
                }else if (key.contains("组织机构代码")){
                    jsonObject.put("organizationalCode",value);
                }else if (key.contains("注册地址")){
                    jsonObject.put("registeredAddress",value);
                }else if (key.contains("法定代表人或者负责人")){
                    jsonObject.put("legalPersonInfo",value);
                }else if (key.contains("负有直接责任的财务负责人")){
                    jsonObject.put("financialPersonInfo",value);
                }else if (key.contains("实际负责人姓名")){
                    jsonObject.put("actualPersonInfo",value);
                }else if (key.contains("负有直接责任的中介机构")){
                    jsonObject.put("actualInfo",value);
                }else if (key.contains("案件性质")){
                    jsonObject.put("caseNature",value);
                }else if (key.contains("主要违法事实")){
                    jsonObject.put("punish",value);
                    /*jsonObject.put("Illegal",value.split("<br>")[0]);
                    jsonObject.put("punish",value.split("<br>")[1]);*/
                }
            }
        }
        resultList.add(jsonObject);
    }

    /*public static Document getResponse(String url, String agent){
        Document document = null;
        try {
            Thread.sleep(SLEEP_TIME);
            document = Jsoup.connect(url).userAgent(agent).get();
        }catch (Exception e){e.printStackTrace();}
        return document;
    }*/
    public static Document getResponse(String url, String agent, String proxy){
        Document document = null;
        try {
            Thread.sleep(SLEEP_TIME);
            if (!StringUtils.isEmpty(proxy) && !StringUtils.isEmpty(agent)){
                document = Jsoup.connect(url).userAgent(agent).proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxy.split(":")[0],Integer.valueOf(proxy.split(":")[1])))).get();
            }else if (!StringUtils.isEmpty(agent)){
                document = Jsoup.connect(url).userAgent(agent).get();
            }else if (!StringUtils.isEmpty(proxy)){
                document = Jsoup.connect(url).proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(proxy.split(":")[0],Integer.valueOf(proxy.split(":")[1])))).get();
            }else {
                document = Jsoup.connect(url).get();
            }
        }catch (Exception e){e.printStackTrace();}
        return document;
    }
}
