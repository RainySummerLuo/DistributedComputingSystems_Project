package ChatServer;

import ChatClient.ClientInterface;

class ChatClient {
    private String name;
    private ClientInterface client;

    // Chat client's constructor
    ChatClient(String name, ClientInterface client) {
        this.name = name;
        this.client = client;
    }

    String getName() {
        return name;
    }

    ClientInterface getClient() {
        return client;
    }
}

