package twitter.monzo.model;

public class SearchMeta {

    String next_results; //next page of results
    String query; //search query
    int count; //results per page?

    public String getNext_results() {
        return next_results;
    }

    public void setNext_results(String next_results) {
        this.next_results = next_results;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
