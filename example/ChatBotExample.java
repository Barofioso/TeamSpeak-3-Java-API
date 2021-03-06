/*
 * #%L
 * TeamSpeak 3 Java API
 * %%
 * Copyright (C) 2014 Bert De Geyter
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

import java.util.logging.Level;

/**
 * A simple chat bot that responds to any channel messages starting with "hello"
 * by greeting the client who sent the message.<br>
 * This bot also includes a simple ping testing feature.
 */
public class ChatBotExample {

	public static void main(String[] args) {
		final TS3Config config = new TS3Config();
		config.setHost("77.77.77.77");
		config.setDebugLevel(Level.ALL);
		config.setLoginCredentials("serveradmin", "serveradminpassword");

		final TS3Query query = new TS3Query(config);
		query.connect();

		final TS3Api api = query.getApi();
		api.selectVirtualServerById(1);
		api.setNickname("PutPutBot");
		api.sendChannelMessage("PutPutBot is online!");

		// Get are own client ID by running the "whoami" command
		// Our client ID was assigned when we selected the virtual server
		final int clientId = api.whoAmI().getId();

		// Listen to chat in the channel the query is currently in
		// As we never changed the channel, this will be the default channel of the server
		api.registerEvent(TS3EventType.TEXT_CHANNEL, 0);
		api.registerEvent(TS3EventType.CHANNEL);

		// Register the event listener
		api.addTS3Listeners(new TS3EventAdapter() {

			@Override
			public void onTextMessage(TextMessageEvent e) {
				// Only react to channel messages not sent by the query itself
				if (e.getTargetMode() == TextMessageTargetMode.CHANNEL && e.getInvokerId() != clientId) {
					String message = e.getMessage().toLowerCase();

					if (message.equals("!ping")) {
						// Answer "!ping" with "pong"
						api.sendChannelMessage("pong");
					} else if (message.startsWith("hello")) {
						// Greet whoever said hello
						// Message: "Hello <client name>!"
						api.sendChannelMessage("Hello " + e.getInvokerName() + "!");
					}
				}
			}
		});
	}
}
