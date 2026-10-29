public class Customer 
{
    private String username;
    private String password;
    private int points;
    private State state;

    public Customer(String username, String password, int points) {
        this.username = username;
        this.password = password;
        this.points = points;
        updateState();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPoints() {
        return points;
    }

    public String getStatus() {
        return state.getStatusName();
    }

    public void setPoints(int points) {
        this.points = Math.max(0, points);//Pick the bigger number between 0 and points
        updateState();
    }

    public void addPoints(int amount) {
        this.points += amount;
        updateState();
    }

    public void redeemPoints(int amount) {
        this.points = Math.max(0, this.points - amount);
        updateState();
    }

    public void updateState() {
        if (points >= 1000) {
            state = new GoldState();
        } else {
            state = new SilverState();
        }
    }
}