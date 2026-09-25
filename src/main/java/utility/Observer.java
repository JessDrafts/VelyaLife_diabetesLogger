package utility;

import model.Notification;

public interface Observer {
    void update(Notification notification);
}
