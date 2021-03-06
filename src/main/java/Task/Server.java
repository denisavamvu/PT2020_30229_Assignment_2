package Task;

import Task.Client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Server extends Thread{
    private BlockingQueue<Client> clients;
    private int waitingPeriod;
    private volatile boolean toRun=true;
    private volatile boolean timePassed=false;
    public Server(){
        clients = new ArrayBlockingQueue<Client>(100);
        waitingPeriod=0;
    }
    public void setToRun(boolean toRun){
        this.toRun=toRun;
    }
    //continutul cozii
    public String toString(){
        String s=this.getName()+" ";
        if(clients.isEmpty())
            s+="closed";
        else
            for(Client client: clients)
                s+=client.toString();
        return s;
    }
    //este coada goala?
    public boolean hasClients(){
        if(!clients.isEmpty())
            return true;
        return false;
    }
    public void setTimePassed(boolean timePassed) {
        this.timePassed = timePassed;
    }
    //daca se trece la urmatorul pas in simulare, se proceseaza primul client, altfel se asteapta
    public synchronized void run(){
        while(toRun){
            if(timePassed)
            {
                this.decFirstClient();
                timePassed=false;

            }
            else
            try{
                wait();
            }
                 catch (InterruptedException e) {
                }
        }
        notifyAll();
    }

    public void addClient(Client client){
        clients.add(client);
        waitingPeriod+=client.getServiceTime();
    }
    public int getWaitingPeriod(){
        return this.waitingPeriod;
    }
    //procesam primul client din coada; se decrementeaza timpul de servire iar daca acesta devine 0,
    //clientul a fost procesat total si eliminat din coada
    public void decFirstClient(){
        if(!clients.isEmpty())
        {
            if(clients.peek().getServiceTime()>0)
                clients.peek().decServiceTime();
            if(clients.peek().getServiceTime()==0)
                clients.remove(clients.peek());
            waitingPeriod--;
        }

    }
    public int getCurrtentWaitingTime(){
        return clients.size();
    }
}
