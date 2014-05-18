package me.masterejay.infobot;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.yetanotherx.reddit.RedditPlugin;
import com.yetanotherx.reddit.api.data.CommentData;
import com.yetanotherx.reddit.api.data.LinkData;
import com.yetanotherx.reddit.api.modules.ExternalDomain;
import com.yetanotherx.reddit.api.modules.RedditCore;
import com.yetanotherx.reddit.api.modules.RedditLink;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author MasterEjay
 */
public class GitHubBot extends RedditPlugin {

	String[] params;
	static String username;
	static String password;
	public GitHubBot() {

	}


	@SuppressWarnings("unchecked")
	public void run() throws InterruptedException {

		RedditCore.newFromUserAndPass(this, username, password).doLogin();
		params = null;
		ExternalDomain dom = ExternalDomain.newFromDomain(this,"github.com");
		for (LinkData link : dom.getUsages()) {

			RedditLink newLin = RedditLink.newFromLink(this, link);
			//System.out.println(link.getTitle());
			try {
			if (!newLin.getLinkData().getSubreddit().equals("test")){
				throw new Error("Debug error!");
			}
			for (CommentData cd : newLin.getComments()){
				if (cd.getAuthor().equals(username)){
					throw new Error("I was already there!");
					}

				}

			}
			catch(Error e){
				System.out.println(e.getMessage());
				Thread.sleep(1000);
				continue;
			}

			String gURL = newLin.getLinkData().getURL();
			String gitHubAuthor = gURL.split("/")[3];
			String githubTitle = gURL.split("/")[4];
			System.out.println(gitHubAuthor + " "+ githubTitle);
			URL url =null;
			try{
				url = new URL("https://api.github.com/repos/" + gitHubAuthor + "/" + githubTitle);
			}catch(MalformedURLException e){
				e.printStackTrace();
			}
			URLConnection connection = null;
			String jsonResult=null;
			try{
				connection = url.openConnection();
				ByteArrayOutputStream result1 = new ByteArrayOutputStream();
				java.io.InputStream input1 = connection.getInputStream();
				byte[] buffer = new byte[1000];
				int amount = 0;

				while(amount != -1)
				{

					result1.write(buffer, 0, amount);
					amount = input1.read(buffer);

					jsonResult = result1.toString();
				}
			}catch(IOException e){
				e.printStackTrace();
			}

			JSONParser parser= new JSONParser();
			Object obj = null;
			try{
				obj=parser.parse(jsonResult);
			}catch(ParseException e){
				e.printStackTrace();
			}
			JSONObject jsonObj = (JSONObject) obj;
			String language = jsonObj.get("language").toString();
			String forks = jsonObj.get("forks_count").toString();
			String watchers = jsonObj.get("watchers_count").toString();
			String stars =  jsonObj.get("stargazers_count").toString();
			String description;
			if (jsonObj.get("description") != null){
				description =  jsonObj.get("description").toString();
			}
			else {
				description = "None";
			}
			StringBuilder sb = new StringBuilder();
			sb.append("**");
			sb.append(githubTitle);
			sb.append("**");
			sb.append(" Information: ");
			sb.append("\n\n");
			sb.append(" Author: ");
			sb.append(gitHubAuthor);
			sb.append("\n\n");
			sb.append(" Description: ");
			sb.append(description);
			sb.append("\n\n");
			sb.append(" Main Language: ");
			sb.append(language);
			sb.append("\n\n");
			sb.append(" Number of Forks: ");
			sb.append(forks);
			sb.append("\n\n");
			sb.append(" Number of Watchers: ");
			sb.append(watchers);
			sb.append("\n\n");
			sb.append(" Number of Stars: ");
			sb.append(stars);
			sb.append("\n\n");

			newLin.doReply(sb.toString());

		}
		int WAIT_TIME = 5000;
		System.out.println("Welp, thats a wrap! Running again in " + WAIT_TIME + " milliseconds!");
		Thread.sleep(WAIT_TIME);
		run();
	}

	public static void main(String[] args) {
		username = args[0];
		password = args[1];
		try {
			new GitHubBot().run();
		} catch (InterruptedException ex) {
			Logger.getLogger(GitHubBot.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	@Override
	public String getName(){
		return null;
	}

	@Override
	public String getVersion(){
		return null;
	}
}
