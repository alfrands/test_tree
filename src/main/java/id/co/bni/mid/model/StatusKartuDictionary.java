package id.co.bni.mid.model;

public class StatusKartuDictionary {

    private String blockCode;
    private String title;
    private String displayCode;
    private String colorCode;
    private String description;

    public StatusKartuDictionary(String blockCode, String title, String displayCode, String colorCode, String description) {
        this.blockCode = blockCode;
        this.title = title;
        this.displayCode = displayCode;
        this.colorCode = colorCode;
        this.description = description;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
