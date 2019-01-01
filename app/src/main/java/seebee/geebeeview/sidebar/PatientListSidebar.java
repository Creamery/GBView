package seebee.geebeeview.sidebar;

import android.support.constraint.ConstraintLayout;
import android.widget.Button;

import java.util.ArrayList;

public class PatientListSidebar extends SidebarParent {
    private Button btnSidebarAddDataset, btnSidebarAddFilter, btnSidebarHPIList, btnSidebarPatientList, btnSidebarBack;
    private ConstraintLayout contSidebarBlank;
    private ArrayList<ConstraintLayout> itemsSidebarExtend;

    public PatientListSidebar(Button btnOpen, Button btnExtend,
                              Button btnAddDataset, Button btnAddFilter, Button btnHPIList, Button btnPatientList,
                              ConstraintLayout contBlank, Button btnBack) {
        super(btnOpen, btnExtend);

        setBtnSidebarAddDataset(btnAddDataset);
        setBtnSidebarAddFilter(btnAddFilter);
        setBtnSidebarHPIList(btnHPIList);
        setBtnSidebarPatientList(btnPatientList);

        this.setContSidebarBlank(contBlank);
        this.setBtnSidebarBack(btnBack);
    }

    public Button getBtnSidebarBack() {
        return btnSidebarBack;
    }

    public void setBtnSidebarBack(Button btnSidebarBack) {
        this.btnSidebarBack = btnSidebarBack;
    }

    public ConstraintLayout getContSidebarBlank() {
        return contSidebarBlank;
    }

    public void setContSidebarBlank(ConstraintLayout contSidebarBlank) {
        this.contSidebarBlank = contSidebarBlank;
    }

    public Button getBtnSidebarAddDataset() {
        return btnSidebarAddDataset;
    }

    public void setBtnSidebarAddDataset(Button btnSidebarAddDataset) {
        this.btnSidebarAddDataset = btnSidebarAddDataset;
    }

    public Button getBtnSidebarAddFilter() {
        return btnSidebarAddFilter;
    }

    public void setBtnSidebarAddFilter(Button btnSidebarAddFilter) {
        this.btnSidebarAddFilter = btnSidebarAddFilter;
    }

    public Button getBtnSidebarHPIList() {
        return btnSidebarHPIList;
    }

    public void setBtnSidebarHPIList(Button btnSidebarHPIList) {
        this.btnSidebarHPIList = btnSidebarHPIList;
    }

    public Button getBtnSidebarPatientList() {
        return btnSidebarPatientList;
    }

    public void setBtnSidebarPatientList(Button btnSidebarPatientList) {
        this.btnSidebarPatientList = btnSidebarPatientList;
    }

    public ArrayList<ConstraintLayout> getItemsSidebarExtend() {
        return itemsSidebarExtend;
    }

    public void setItemsSidebarExtend(ArrayList<ConstraintLayout> itemsSidebarExtend) {
        this.itemsSidebarExtend = itemsSidebarExtend;
    }
}
