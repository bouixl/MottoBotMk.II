package commands;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PictureThread implements Runnable {

	private static final int YANDERE = 0;
	private static final int KONACHAN = 1;
	private static final int SANKAKU = 2;
	private static final int DANBOORU = 3;
	private static final int MAX_TRIES = 4;
	public static final Color EXPLICIT = new Color(255, 20, 147);
	public static final Color SAFE = new Color(50, 205, 50);
	public static final Color QUESTIONABLE = new Color(255, 165, 0);
	public static final List<String> IMAGE_TYPES = Collections.unmodifiableList(Arrays.asList(".png", ".jpg", ".jpg", ".jpeg", ".gif", ".bmp"));

	private Random rand;
	private String arguments;

	private MessageReceivedEvent e;

	public PictureThread(MessageReceivedEvent e, String args) {
		this.rand = new Random();
		this.e = e;
		this.arguments = args.replace(" ", "+").toLowerCase();
		this.arguments = this.arguments.replace("ademage", "nico_robin");
	}

	@Override
	public void run() {
		this.e.getChannel().sendTyping().queue();
		boolean channelIsNSFW = this.e.getChannel().getName().toLowerCase().contains("nsfw") || this.e.getTextChannel().isNSFW();

		int selector = this.rand.nextInt(MAX_TRIES);
		int nbRecherche = 0;
		Document doc;
		String searchUrl = null;
		String pageUrl = null;
		String imageUrl = null;
		String fullImageUrl = null;

		while(nbRecherche<MAX_TRIES) {
			switch(selector) {
				case YANDERE: // Yande.re
					searchUrl = "https://yande.re/post?tags=order:random";
					break;
				case KONACHAN: // Konachan
					searchUrl = "http://konachan.com/post?tags=order:random";
					break;
				case SANKAKU: // chan.sankaku
					searchUrl = "https://chan.sankakucomplex.com/?tags=order:random";
					break;
				case DANBOORU: // danbooru.donmai
					searchUrl = "https://danbooru.donmai.us/posts?tags=order:random";
					break;
				default:
					break;
			}

			if(this.arguments==null) {
				this.arguments = "";
			}
			searchUrl += "+" + this.arguments;

			if(channelIsNSFW==false) {  // Si le salon n'est pas NSFW, restreindre le contenu{
				//System.out.println("sfw chan");
				searchUrl += "+rating:safe-rating:e";
			}

			try {
				doc = Jsoup.connect(searchUrl).ignoreHttpErrors(true).get();

				if (selector==SANKAKU) {
					//System.out.println("searchurl : "+searchUrl);
					//System.out.println(doc.html());
					Elements elems = doc.select("span[class=thumb blacklisted] > a");
					if(elems.size()>0) {
						//System.out.println("ok");
						int selectedA = this.rand.nextInt(elems.size());
						pageUrl = "https://chan.sankakucomplex.com" + elems.get(selectedA).attr("href");
					}
					else {
						pageUrl = null;
					}
				}
				else if (selector==DANBOORU) {
					Elements elems =  doc.select("article");
					if(elems.size()>0) {
						int selectedA = this.rand.nextInt(elems.size());
						pageUrl = "https://danbooru.donmai.us/posts/" + elems.get(selectedA).attr("data-id");
					}
					else {
						pageUrl = null;
					}
				}
				else {
					pageUrl = doc.select("span[class=plid]").stream().findAny().map(docs -> docs.html()).orElse(null);
					if(pageUrl!=null) {
						pageUrl = pageUrl.substring(4);
					}
				}

				if(pageUrl!=null) {
					doc = Jsoup.connect(pageUrl).get();
					fullImageUrl = doc.select("a.sample[id=image-link]").stream().findFirst().map(docs -> docs.absUrl("href").trim()).orElse(null);
					imageUrl = doc.select("img[id=image]").stream().findFirst().map(docs -> docs.absUrl("src").trim()).orElse(null);
				}
				else {
					doc = null;
					fullImageUrl = null;
					imageUrl = null;
				}
			}
			catch (IOException | NullPointerException err) {
				err.printStackTrace();
				doc = null;
				pageUrl = null;
				fullImageUrl = null;
				imageUrl = null;
			}

			if (imageUrl != null) {
				EmbedBuilder eb = new EmbedBuilder();

				String title = this.arguments.replace("+", " ");
				if(this.arguments=="") {
					title = "Motto !";
				}

				if(channelIsNSFW) {
					eb.setTitle(title, pageUrl);
					eb.setColor(EXPLICIT);
				}
				else {
					eb.setTitle(title, null);
					eb.setColor(SAFE);
				}

				eb.setDescription("Demandé par " + this.e.getMember().getEffectiveName());

				if(fullImageUrl != null) {
					imageUrl = fullImageUrl;
					System.out.println(this.e.getAuthor().getName() + " " + this.arguments +" : (full) " + imageUrl);
				}
				else {
					System.out.println(this.e.getAuthor().getName() + " " + this.arguments +" : " + imageUrl);
				}


				InputStream file;
				String filename;
				String filetype = ".png";

				for(String ext : IMAGE_TYPES) {
					if(imageUrl.contains(ext))
					{
						filetype = ext;
						break;
					}
				}
				filename = String.valueOf(rand.nextInt(2000000000))+filetype;
				eb.setImage("attachment://"+filename);

				try {
			        HttpURLConnection httpcon = (HttpURLConnection) new URL(imageUrl).openConnection();
			        httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
					file = httpcon.getInputStream();
					this.e.getChannel().sendFile(file, filename).embed(eb.build()).queue();
				}
				catch (IOException e1) {
					this.e.getChannel().sendMessage("Erreur lors de la récupération de l'image :(").queue();
					e1.printStackTrace();
				}

				break;
			}
			else {
				selector = (selector + 1)%MAX_TRIES;
				nbRecherche++;
			}

			if(nbRecherche>=MAX_TRIES) {
				this.messageErreur();
			}
		}
	}

	private void messageErreur() {
		this.e.getChannel().sendMessage("Ce tag n'existe pas <@"+this.e.getAuthor().getId()+">").queue();
	}

    /*private static void getImage(String src, String filename) throws IOException {
        URL url = new URL(src);

        HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
        InputStream in = httpcon.getInputStream();

        OutputStream out = new BufferedOutputStream(new FileOutputStream("mottoImages\\"+filename));

        for (int b; (b = in.read()) != -1;) {
            out.write(b);
        }
        out.close();
        in.close();
    }*/
}
