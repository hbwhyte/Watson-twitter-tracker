package twitter.monzo.model;

public class MonzoResponse {

    Statuses[] statuses;
    SearchMeta meta;

    public Statuses[] getStatuses() {
        return statuses;
    }

    public void setStatuses(Statuses[] statuses) {
        this.statuses = statuses;
    }

    public SearchMeta getMeta() {
        return meta;
    }

    public void setMeta(SearchMeta meta) {
        this.meta = meta;
    }
}