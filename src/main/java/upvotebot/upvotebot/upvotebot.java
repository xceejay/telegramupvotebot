package upvotebot.upvotebot;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class upvotebot extends TelegramLongPollingBot {

	@Override
	public void onUpdateReceived(Update update) {

		// check if the update has a message
		if (update.hasMessage() && update.getMessage().hasText()) {

			Message message = update.getMessage();
			SendMessage firstMessage = new SendMessage();
			SendMessage msg = new SendMessage();
			msg.setChatId(message.getChatId().toString());
			firstMessage.setChatId(message.getChatId().toString());

			try {

				if ((message.getText().startsWith("/start@tgupvote_bot") || message.getText().startsWith("/start"))) {
					firstMessage.setText("Hi, you just started me: send '!help' to know my use.");
					execute(firstMessage);
				} else if (message.getText().startsWith("!help")) {
					firstMessage.setText(
							"* Reply a message you liked in a group with ++ / +1 to increase vote count/reputation of the user who sent the message."
									+ "\n\n* Reply a message you disliked in a group with -- / -1 to decrease vote count/reputation of user who sent the message."
									+ "\n\n* Reply a message of a user with '!points' to see how many points that user has.");
					execute(firstMessage);
				}

				User sender = update.getMessage().getFrom();
				User user = update.getMessage().getReplyToMessage().getFrom();
				File file = new File("/home/joel/igbotvotes");
				String username = user.getUserName();
				if (message.getText().startsWith("!points")) {
					firstMessage.setText(ReadFromFile(file, username));
					execute(firstMessage);
				} else if (message.hasText()

						&& ((message.getText().contentEquals("--")) || (message.getText().contentEquals("-1")
								|| message.getText().matches("\\-\\-") || message.getText().matches("\\-1")))) {
					if (sender.getUserName().matches(username)) {
						msg.setText("Sorry,You Cannot Down Vote Yourself.");
						execute(msg);
					} else if (CheckFile(file, username) == false) {
						msg.setText("This is " + username + "'s first vote and it was a downvote: -1" + " given by"
								+ sender.getUserName());
						execute(msg);
						writeToFile(username, file);
					} else if (CheckFile(file, username) == true) {
						setNewScoreMinus(file, username);
						msg.setText(ReadFromFile(file, username) + ":-1 downvote by " + sender.getUserName());
						execute(msg);

					}
				}

				else if (message.hasText()
						&& ((message.getText().contentEquals("++")) || (message.getText().contentEquals("+1")
								|| message.getText().matches("++") || message.getText().matches("+1")))) {
					if (sender.getUserName().matches(username)) {
						msg.setText("Sorry,You Cannot Vote For Yourself");
						execute(msg);
					} else if (CheckFile(file, username) == false) {
						msg.setText("This is " + username + "'s first vote: +1" + " given by" + sender.getUserName());
						execute(msg);
						writeToFile(username, file);
					} else if (CheckFile(file, username) == true) {
						setNewScore(file, username);
						msg.setText(ReadFromFile(file, username) + ":+1 vote by " + sender.getUserName());
						execute(msg);

					}
				}

			} catch (TelegramApiException e) {
				e.printStackTrace();
			}

			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeToFile(String username, File file) throws Exception {
		Writer write = new BufferedWriter(new FileWriter(file, true));
		int score = getScore(file, username) + 1;
		write.append(username + " " + score + "\n");
		write.close();
	}

	public static String ReadFromFile(File file, String username) throws Exception {

		return username + " currently has " + getScore(file, username) + " points.";
	}

	public static boolean CheckFile(File file, String username) throws Exception {
		Scanner input = new Scanner(file);
		int i = 0;
		while (input.hasNext()) {
			String Line = input.nextLine();
			if (Line.contains(username) || Line.startsWith(username) || input.next().matches(username)
					|| input.next().contains(username)) {
				i = 1;
				break;
			}

		}
		if (i < 1)
			return false;
		else
			return true;
	}

	public static int getScore(File file, String username) throws Exception {
		int score = 0;
		Scanner input = new Scanner(file);
		while (input.hasNextLine()) {
			String Line = input.nextLine();
			if (Line.contains(username)) {
				score = getNumbers(Line);
				break;
			}
		}
		return score;
	}

	public static int getNumbers(String Line) {
		String n = "";

		int numbers = Integer.parseInt(Line.split(" ")[1]);

		return numbers;

	}

	public static void setNewScore(File file, String username) throws Exception {
		Scanner input = new Scanner(file);
		int i = 0;
		while (input.hasNextLine()) {
			String Line = input.nextLine();
			if (Line.contains(username)) {
				i = getNumbers(Line);
				i = i + 1;
				String oldLine = Line;
				Line = replaceLast(Line, String.valueOf(getNumbers(Line)), String.valueOf(i));
				replaceLines(oldLine, Line, file);
				break;
			}
		}

	}

	public static void setNewScoreMinus(File file, String username) throws Exception {
		Scanner input = new Scanner(file);
		int i = 0;
		while (input.hasNextLine()) {
			String Line = input.nextLine();
			if (Line.contains(username)) {
				i = getNumbers(Line);
				i = i - 1;
				String oldLine = Line;
				Line = replaceLast(Line, String.valueOf(getNumbers(Line)), String.valueOf(i));
				replaceLines(oldLine, Line, file);
				break;
			}
		}

	}

	public static void replaceLines(String OldLine, String NewLine, File file) throws Exception {

		Path path = Paths.get(file.getPath());
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll(OldLine, NewLine);
		Files.write(path, content.getBytes(charset));

	}

	public static String replaceLast(String string, String toReplace, String replacement) {
		int pos = string.lastIndexOf(toReplace);

		if (pos > -1) {
			return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
		} else {
			return string;
		}
	}

	@Override
	public String getBotUsername() {

		return "tgupvote_bot";
	}

	@Override
	public String getBotToken() {

		return "989068293:AAEY6hjdkjvjeeg0x5dF7Q9K01dKRK3i3jU";
	}

}