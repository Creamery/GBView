package seebee.geebeeview.sidebar;

import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class SidebarParent{
    protected Button btnOpenSidebar;
    protected boolean isSidebarOpen;
    protected ArrayList<ConstraintLayout> itemsSidebarExtend;

    public SidebarParent(Button btnOpen) {
        this.setBtnOpenSidebar(btnOpen);
        this.isSidebarOpen = false;
        this.itemsSidebarExtend = new ArrayList<ConstraintLayout>();
    }

    public void toggleSidebar() {
        isSidebarOpen = !isSidebarOpen;
    }

    public int getSidebarVisibility(boolean visibility) {
        if(visibility) {
            return View.VISIBLE;
        }
        else {
            return View.INVISIBLE;
        }
    }


    public boolean isSidebarOpen() {
        return isSidebarOpen;
    }

    public void setSidebarOpen(boolean sidebarOpen) {
        isSidebarOpen = sidebarOpen;
    }

    public Button getBtnOpenSidebar() {
        return btnOpenSidebar;
    }

    public void setBtnOpenSidebar(Button btnOpenSidebar) {
        this.btnOpenSidebar = btnOpenSidebar;
    }

    public ArrayList<ConstraintLayout> getItemsSidebarExtend() {
        return itemsSidebarExtend;
    }

    public void setItemsSidebarExtend(ArrayList<ConstraintLayout> itemsSidebarExtend) {
        this.itemsSidebarExtend = itemsSidebarExtend;
    }
}
