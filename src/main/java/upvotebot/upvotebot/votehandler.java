package upvotebot.upvotebot;


public class votehandler extends upvotebot{
	private int voteCount=0;
	private int id = 0;
  
   

    public votehandler() {
     
    }

    public votehandler(int newID, int newVoteCount) {
        id = newID;
        voteCount = newVoteCount;
      
    }

    /**
     * @return the dateCreated
     */
    

    /**
     * @param balance the balance to set
     */
    public void setVoteCount(int voteCount) {
        this.voteCount=voteCount;
    }

    /**
     * @return the balance
     */
    public int getvoteCount() {
        return voteCount;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }



    public void voteCounterPlus() {
        voteCount ++;
    }

    public void voteCounterMinus() {
        voteCount-- ;
    }

}

