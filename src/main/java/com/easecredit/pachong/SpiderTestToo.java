package com.easecredit.pachong; /**
 * @Author: GaoXing
 * @Description
 * @data: Created by GaoXing on 2019/3/19.
 * @Modified By;
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiderTestToo {
    private  static Logger logger = LoggerFactory.getLogger(SpiderTestToo.class);
    private final static String START_URL = "http://hd.chinatax.gov.cn/xxk/action/ListXinxikucomXml.do?dotype=casetime&id=@YEAR@年@MONTH@月";
    private final static String SECOND_URL = "http://hd.chinatax.gov.cn/xxk/action/GetArticleView1.do?op=xxkweb&id=@ITEMID@";
    private final static String AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";

    public static void main(String[] args){

        attentionSourceTest();
    }

    public static void attentionSourceTest(){
        for (int year = 2014 ; year < 2019 ; year++){
            for (int month = 1 ; month < 13 ; month++){
                String url_1 = START_URL.replaceAll("@YEAR@",String.valueOf(year)).replaceAll("@MONTH@",String.valueOf(month));
                Document response = getResponse(url_1, AGENT);
                if (response != null) {
                    Elements items = response.getElementsByTag("root").get(0).getElementsByTag("item");
                    for (Element item : items) {
                        String itemId = item.attr("id");
                        String url_2 = SECOND_URL.replaceAll("@ITEMID@", itemId);
                        Document table = getResponse(url_1, AGENT);
                        if (table != null) {
                            //TODO 开始解析
                            logger.info("");

                            System.out.println(table.toString());
                        } else {
                            System.out.println("请求失败,请求url为:" + url_2);
                        }
                    }
                }else {
                    System.out.println("请求失败,请求url为:" + url_1);
                }
            }
        }
    }

    public static Document getResponse(String url, String agent){
        Document document = null;
        try {
            document = Jsoup.connect(url).userAgent(agent).get();
        }catch (Exception e){e.printStackTrace();}
        return document;
    }

}