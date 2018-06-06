package afyabora.xeonite.com.myapplication.pojos;

/**
 * Created by hornellama on 03/06/2018.
 */

public class CountryData {
    private int id;
    private String codeISO;
    private String countryName;
    private String Town;
    private String Content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodeISO() {
        return codeISO;
    }

    public void setCodeISO(String codeISO) {
        this.codeISO = codeISO;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
