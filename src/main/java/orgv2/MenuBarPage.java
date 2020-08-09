package main.java.orgv2;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
public class MenuBarPage extends JMenuBar {

    private Map<String, JMenuPageInfo> menuMap;

    public void setMenuMap(Map<String, JMenuPageInfo> menuMap) {
        this.menuMap = menuMap;
    }

    public MenuBarPage() {
        this(new HashMap<>());
    }


    public MenuBarPage(Map<String, JMenuPageInfo> menuMap) {
        this.menuMap = menuMap;
        init();
    }

    private void init() {
        initMenu();
    }

    private void initMenu() {
        Iterator<Map.Entry<String, JMenuPageInfo>> iterable = menuMap.entrySet().iterator();
        while (iterable.hasNext()) {
            Map.Entry<String, JMenuPageInfo> entry = iterable.next();
            String key = entry.getKey();
            JMenuPageInfo value = entry.getValue();
            List<JMenuItemPage> jMenuItemPageList = value.getMenuItemInfoList() == null ? Collections.emptyList() : getMenuItemList(value.getMenuItemInfoList());
            JMenuPage jMenuPage = new JMenuPage(key, jMenuItemPageList);
            jMenuPage.addMenuListener(value.getMenuListener());
            this.add(jMenuPage);
        }
    }

    private List<JMenuItemPage> getMenuItemList(List<MenuItemInfo> value) {
        Objects.requireNonNull(value);
        return value.stream().map(JMenuItemPage::new).collect(Collectors.toList());
    }

    protected static class JMenuPageInfo {
        private MenuListener menuListener;
        private List<MenuItemInfo> menuItemInfoList;

        public JMenuPageInfo(MenuListener menuListener, List<MenuItemInfo> menuItemInfoList) {
            this.menuListener = menuListener;
            this.menuItemInfoList = menuItemInfoList;
        }

        public JMenuPageInfo(MenuListener menuListener) {
            this(menuListener, null);
        }

        public MenuListener getMenuListener() {
            return menuListener;
        }

        public List<MenuItemInfo> getMenuItemInfoList() {
            return menuItemInfoList;
        }
    }

    protected static class MenuListenerImpl implements MenuListener {
        private Done done;

        public MenuListenerImpl(Done done) {
            this.done = done;
        }

        /**
         * Invoked when a menu is selected.
         *
         * @param e a MenuEvent object
         */
        @Override
        public void menuSelected(MenuEvent e) {
            try {
                done.done();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Invoked when the menu is deselected.
         *
         * @param e a MenuEvent object
         */
        @Override
        public void menuDeselected(MenuEvent e) {

        }

        /**
         * Invoked when the menu is canceled.
         *
         * @param e a MenuEvent object
         */
        @Override
        public void menuCanceled(MenuEvent e) {

        }
    }


    protected class JMenuItemPage extends JMenuItem {
        private MenuItemInfo menuItemInfo;

        public JMenuItemPage(MenuItemInfo menuItemInfo) {
            super(menuItemInfo.getMeanItemName(), null);
            addActionListener(menuItemInfo.getActionListener());
        }
    }

    protected class JMenuPage extends JMenu {
        private List<JMenuItemPage> jMenuItemPageList;

        private String name;

        public JMenuPage(String name, List<JMenuItemPage> jMenuItemPageList) {
            super(name);
            this.jMenuItemPageList = jMenuItemPageList;
            initJMenuItemList();
        }

        private void initJMenuItemList() {
            jMenuItemPageList.forEach(item -> {
                add(item);
            });
        }
    }


    protected class MenuItemInfo {
        private String meanItemName;
        private ActionListener actionListener;

        public MenuItemInfo(String meanItemName, ActionListener actionListener) {
            this.meanItemName = meanItemName;
            this.actionListener = actionListener;
        }

        public String getMeanItemName() {
            return meanItemName;
        }

        public void setMeanItemName(String meanItemName) {
            this.meanItemName = meanItemName;
        }

        public ActionListener getActionListener() {
            return actionListener;
        }

        public void setActionListener(ActionListener actionListener) {
            this.actionListener = actionListener;
        }

        @Override
        public String toString() {
            return "MenuItemInfo{" +
                    "meanItemName='" + meanItemName + '\'' +
                    ", actionListener=" + actionListener +
                    '}';
        }
    }

    @FunctionalInterface
    public interface Done {
        void done() throws Exception;
    }
}
