package xyz.imxqd.clickclick.func;

import android.app.Notification;
import android.app.PendingIntent;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.imxqd.clickclick.App;
import xyz.imxqd.clickclick.R;
import xyz.imxqd.clickclick.model.AppEventManager;
import xyz.imxqd.clickclick.service.NotificationCollectorService;
import xyz.imxqd.clickclick.utils.NotificationAccessUtil;

public class NotificationFunction extends AbstractFunction {
    public static final String PREFIX = "notification";

    private static final String REGEX_PACKAGE = "([a-zA-Z0-9_]+\\.{1})+[a-zA-Z0-9_]+";

    public NotificationFunction(String funcData) {
        super(funcData);
    }

    private String getPackageName(String args) {
        if (TextUtils.isEmpty(args)) {
            return null;
        }
        int pos = args.indexOf(':');
        if (pos <= 0) {
            throw new RuntimeException("Syntax Error");
        }
        String packageName = args.substring(0, pos);
        Pattern pattern = Pattern.compile(REGEX_PACKAGE);
        Matcher matcher = pattern.matcher(packageName);
        if (matcher.matches()) {
            return packageName;
        } else {
            throw new RuntimeException("Syntax Error");
        }
    }

    public int getOrder(String args) {
        if (TextUtils.isEmpty(args)) {
            return -1;
        }
        int pos = args.indexOf(':');
        if (pos <= 0) {
            throw new RuntimeException("Syntax Error");
        }
        if (pos >= args.length() - 1) {
            throw new RuntimeException("Syntax Error");
        }
        try {
            int order = Integer.valueOf(args.substring(pos + 1));
            return order;
        } catch (Exception e) {
            throw new RuntimeException("Syntax Error" );
        }
    }

    @Override
    public void doFunction(String args) throws Exception {
        NotificationCollectorService service = AppEventManager.getInstance().getNotificationService();
        if (service != null) {
            List<Notification> notifications = service.getNotificationsByPackage(getPackageName(args));
            if (notifications.size() == 0) {
                throw new RuntimeException("There are no notifications of " + getPackageName(args));
            }
            List<PendingIntent> intents = NotificationAccessUtil.getPendingIntents(notifications.get(0));
            int order = getOrder(args);
            if (order > 0 && intents.size() > order) {
                intents.get(order).send();
            }
        } else {
            toast(App.get().getString(R.string.notification_service_error));
            throw new RuntimeException(App.get().getString(R.string.notification_service_error));
        }
    }
}
