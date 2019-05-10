package ChatServer;

import ChatClient.ClientInterface;

class Chat {
    private String name;
    private ClientInterface client;


    //constructor
    Chat(String name, ClientInterface client){
        this.name = name;
        this.client = client;
    }


    //getters and setters
    String getName(){
        return name;
    }


    ClientInterface getClient(){
        return client;
    }
}

