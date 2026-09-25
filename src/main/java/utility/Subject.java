package utility;

import model.Notification;

public interface Subject {
    void addObserver(String username, Observer observer);
    void removeObserver(String username);
    void notifyObservers(Notification notification);
}
