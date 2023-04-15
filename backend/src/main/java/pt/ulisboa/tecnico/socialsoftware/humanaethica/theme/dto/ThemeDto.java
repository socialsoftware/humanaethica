package pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto;

public class ThemeDto {

    private Integer id;
    private String name;
    private List<Activity> activities = new ArrayList<>();
    private List<Institution> institutions = new ArrayList<>();

    public Theme(Integer id, String name, ArrayList<Activity> activities, ArrayList<Institution> institutions) {
        setId(id);
        setName(name);
        setActivities(activities);
        setInstitutions(institutions);
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities){
        this.activities = activities;
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }
}