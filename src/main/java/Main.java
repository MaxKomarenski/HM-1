import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        // makes a connect between program and web-site.
        Document doc = Jsoup.connect("https://bt.rozetka.com.ua/refrigerators/c80125/filter/").get();

        // goes to current classes and gets list of elements of this classes.
        Elements divs_name = doc.getElementsByAttributeValue("class", "g-i-tile-i-title clearfix");
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> lst = new ArrayList<String>();

        //goes of each element of list and take a NAME of refrigerator.
        divs_name.forEach(div_el -> {
            Element aElement = div_el.child(0);
            names.add(aElement.text());
            lst.add(aElement.attr("href"));
        });

        for(int el = 0; el < names.size(); el++) {
            String str_link = lst.get(el);
            str_link = str_link + "comments/page=1/";//go in page with comments
            Document pages = Jsoup.connect(str_link).get();
            Elements count_of_pages = pages.getElementsByAttributeValue("class", "novisited paginator-catalog-l-link");

            ArrayList<String> digits = new ArrayList<>();

            count_of_pages.forEach(i -> {
                digits.add(i.text());
            });

            int last_pages = Integer.parseInt(digits.get(digits.size()-1));// Now I know how many pages of reviews are there.

            ArrayList<String> reviews_lst = new ArrayList<>();
            ArrayList<String> names_lst = new ArrayList<>();
            Map<String, String> dict_names_reviews = new HashMap<>();

            for(int j = 1; j <= last_pages; j++){
                String str_link_com = lst.get(el) + "comments/page=" + Integer.toString(j) + "/";
                Document reviews_and_names = Jsoup.connect(str_link_com).get();

                Elements reviews = reviews_and_names.getElementsByAttributeValue("class", "pp-review-text-i");
                Elements name = reviews_and_names.getElementsByAttributeValue("class", "pp-review-author-name");


                reviews.forEach(rev ->{
                    if(!(rev.text().contains("Достоинства: ") || rev.text().contains("Недостатки: "))){
                        reviews_lst.add(rev.text());
                    }
                });

                name.forEach(name_el -> {
                    names_lst.add(name_el.text());
                });

                for(int el_dict = 0; el_dict < names_lst.size(); el_dict++){
                    dict_names_reviews.put(names_lst.get(el_dict), reviews_lst.get(el_dict));
                }
            }
            System.out.println(dict_names_reviews);

            // Puts out information and writing in .csv file.
            /*
            String names_of_fiels = names.get(el) + ".csv";
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(names_of_fiels), "Cp1251"));
            for(int t = 0; t < names_lst.size(); t++){
                pw.println( names_lst.get(t) + " -> Comment:  " + dict_names_reviews.get(names_lst.get(t)));
                pw.println();
            }
            */
        }
    }
}
