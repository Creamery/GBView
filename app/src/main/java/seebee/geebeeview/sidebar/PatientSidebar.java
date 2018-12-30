package seebee.geebeeview.sidebar;

import android.support.constraint.ConstraintLayout;
import android.widget.Button;

import java.util.ArrayList;

public class PatientSidebar extends SidebarParent {
    private Button btnSidebarAbout, btnSidebarHPI, btnSidebarImmunization;
    private ArrayList<ConstraintLayout> itemsSidebarExtend;

    public PatientSidebar(Button btnOpen, Button btnAbout, Button btnHPI, Button btnImmunization) {
        super(btnOpen);
        this.setBtnSidebarAbout(btnAbout);
        this.setBtnSidebarHPI(btnHPI);
        this.setBtnSidebarImmunization(btnImmunization);
    }

    public Button getBtnSidebarAbout() {
        return btnSidebarAbout;
    }

    public void setBtnSidebarAbout(Button btnSidebarAbout) {
        this.btnSidebarAbout = btnSidebarAbout;
    }

    public Button getBtnSidebarHPI() {
        return btnSidebarHPI;
    }

    public void setBtnSidebarHPI(Button btnSidebarHPI) {
        this.btnSidebarHPI = btnSidebarHPI;
    }

    public Button getBtnSidebarImmunization() {
        return btnSidebarImmunization;
    }

    public void setBtnSidebarImmunization(Button btnSidebarImmunization) {
        this.btnSidebarImmunization = btnSidebarImmunization;
    }

    public ArrayList<ConstraintLayout> getItemsSidebarExtend() {
        return itemsSidebarExtend;
    }

    public void setItemsSidebarExtend(ArrayList<ConstraintLayout> itemsSidebarExtend) {
        this.itemsSidebarExtend = itemsSidebarExtend;
    }
}
