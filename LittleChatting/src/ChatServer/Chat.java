package ChatServer;

import ChatClient.ClientInterface;

class Chat {
    private String name;
    private ClientInterface client;

    // Chat client's constructor
    Chat(String name, ClientInterface client) {
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

