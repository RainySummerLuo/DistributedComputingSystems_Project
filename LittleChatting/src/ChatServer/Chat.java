package ChatServer;

import ChatClient.ClientInterface;

public class Chat {

    public String name;
    public ClientInterface client;

    //constructor
    public Chat(String name, ClientInterface client){
        this.name = name;
        this.client = client;
    }


    //getters and setters
    public String getName(){
        return name;
    }
    public ClientInterface getClient(){
        return client;
    }


}

