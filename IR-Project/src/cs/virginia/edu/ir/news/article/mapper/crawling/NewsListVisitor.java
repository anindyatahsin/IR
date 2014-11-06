package cs.virginia.edu.ir.news.article.mapper.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsListVisitor {
	
	public static void main(String args[]) throws Exception {	
		Document page = Jsoup.connect("http://query.nytimes.com/search/sitesearch/?action=click&contentCollection&region=TopBar&WT.nav=searchWidget&module=SearchSubmit&pgtype=Homepage#/politics/365days/").get();
		Element element = page.select(".searchResults").first();
		Elements elements = element.select(".element2");
	}
}
