package seebee.geebeeview.spinner;

public class CustomSpinnerItem {
    String text;
    Integer imageId;
    public CustomSpinnerItem(String text, Integer imageId){
        this.text = text;
        this.imageId = imageId;
    }

    public String getText(){
        return text;
    }

    public Integer getImageId(){
        return imageId;
    }
}