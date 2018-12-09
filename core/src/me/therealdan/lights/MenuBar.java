package me.therealdan.lights;

import me.therealdan.lights.ui.view.Viewable;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class MenuBar extends JFrame implements MenuListener {

    public MenuBar() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        JMenuBar jMenuBar = new JMenuBar();
        for (Viewable viewable : LightsCore.getInstance().getViewBar().getViews()) {
            JMenu jMenu = new JMenu(viewable.getName());
            jMenu.setName(viewable.getName());
            jMenu.addMenuListener(this);
            jMenuBar.add(jMenu);
        }

        this.setJMenuBar(jMenuBar);
        this.setVisible(true);
        this.setVisible(false);
    }

    @Override
    public void menuSelected(MenuEvent e) {
        JMenu menu = (JMenu) e.getSource();
        LightsCore.getInstance().getViewBar().setActiveTab(Viewable.byName(menu.getName()));
    }

    @Override
    public void menuDeselected(MenuEvent e) {

    }

    @Override
    public void menuCanceled(MenuEvent e) {

    }
}
