import com.sun.tools.javac.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * Created by sahinfurkan on 23/10/15.
 */
public class write {
    public static void main(String[] args) throws IOException {

        final int GRAPH_SIZE = 1000;
        HashMap<String, Integer> indexes = new HashMap<>();
        HashMap<Integer, String> indexesKey = new HashMap<>();
        HashMap<Integer, Boolean> students = new HashMap<>();
        int[] lst = new int[10];
        int[] sizes = new int[10];
        String res = "{\"nodes\":[";
        int sum = 0;

        for (int i = 0; i < 10; i++){
            lst[i] = 0;
            sizes[i] = 0;
        }
        for (String lin : Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/list_of_students.csv"))){
            students.put(Integer.parseInt(lin), false);
        }
        // Finds the number of students for each grade interval from 10 to 100 by 10
        for (String line : Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/grades.csv"))){
            String[] tmp = line.split(",");
            int num = Integer.parseInt(tmp[8].substring(0, tmp[8].indexOf(".")));
            if (num >= 10 && students.containsKey(Integer.parseInt(tmp[0]))) {
                lst[(num-10) / 10] += 1;
                sum++;
            }
        }

        System.out.println(sum);

        // Finds the number of students that should be included in 200 nodes for each grade interval
        for (int i = 0; i < 10; i++){
            lst[i] = (int)((((float)lst[i])/sum)*GRAPH_SIZE);
        }

        int a = 0;
        for (String line : Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/grades.csv"))){
            String node = "";
            String[] tmp = line.split(",");
            int num = Integer.parseInt(tmp[8].substring(0, tmp[8].indexOf(".")));

            if (num >= 10 && students.containsKey(Integer.parseInt(tmp[0])) && sizes[(num-10)/10] < lst[(num-10)/10]) {

                sizes[(num-10)/10]++;
                indexes.put(tmp[0], a++);
                indexesKey.put(a-1, tmp[0]);
                node += "{\"id\":\"" + tmp[0] + "\",\n";
                node += "\"name\":\"\",\n";
                node += "\"genre\":\"" + tmp[9] + "\",\n";
                node += "\"price\":\"" + tmp[8] + "\"},\n";

                res += node;
            }
        }

        res = res.substring(0, res.length()-3);
        res += "}";

//        HashMap<Integer, Pair<Integer, String>> forumThreads = getForumThreads(indexesKey);
        HashMap<String, ArrayList<Integer>> forumRelations = getForumRelations(indexes/*, forumThreads*/ );
        int b = 0;
        res += "],\"links\":[";

        HashMap<String, Edge> edges = getEdges(forumRelations);

        for (Edge edge : edges.values()){
            res += "{\"source\":" + edge.source + ", \"target\":" + edge.target +
                    ", \"value\":" + (edge.value*1000) + ", \"week\":" + (edge.week + 1) + "},\n";
        }

        res = res.substring(0, res.length()-2);
        res += "]}";


        PrintWriter writer = new PrintWriter("allData.json", "UTF-8");
        writer.println(res);
        writer.close();
    }

    public static HashMap<String, Edge> getEdges(HashMap<String, ArrayList<Integer>> forumRelations){
        HashMap<String, Edge> edges = new HashMap<>();
        int size = 0;
        Set<String> keysTemp = forumRelations.keySet();
        ArrayList<String> keys = new ArrayList<>();
        keys.addAll(keysTemp);
        for (ArrayList<Integer> users: forumRelations.values()){
            if(users.size() > 1) {
                size += users.size();
            }
        }
        for (String key:keys){
            String week = key.substring(key.indexOf('-') + 1, key.length());
            ArrayList<Integer> commenters = forumRelations.get(key);
            if (commenters.size() >= 2)
            {
                for (int i = 0; i < commenters.size(); ++i){
                    for (int j = i; j < commenters.size(); ++j){
                        String hash = commenters.get(i) + "-" + commenters.get(j) + "-" + week;
                        if (edges.containsKey(hash)){
                            edges.get(hash).value += ((float)1)/ size;
                        }
                        else{
                            edges.put(hash, new Edge(commenters.get(i), commenters.get(j),
                                                    ((float)1)/size, Integer.parseInt(week)));
                        }
                    }
                }
            }
        }
        return edges;

    /*    += ((float)1)/ size */
    }

    public static class Edge{
        public float value;
        public int source;
        public int target;
        public int week;

        public Edge(Integer source, Integer target, float value, int week){
            this.source = source;
            this.target = target;
            this.value = value;
            this.week = week;
        }

    }
    public static HashMap<String, ArrayList<Integer>> getForumRelations(HashMap<String, Integer> indexes/*,HashMap<Integer, Pair<Integer, String>> threads, */) {
        HashMap<String, ArrayList<Integer>> forumRelations = new HashMap<>();
        try {
            List<String> allPosts = Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/thread_page_views.csv"));
            Long first = 1379349617L;
            for (String post : allPosts) {
                String[] tmp = post.split(",");
                int thread_id = Integer.parseInt(tmp[1]);
                int commenter_id = Integer.parseInt(tmp[0]);
                    if (indexes.containsKey(commenter_id + "")) {
                        long week = getWeek(first, Long.parseLong(tmp[2].substring(0, tmp[2].length()-3)));
                        if (forumRelations.containsKey(thread_id + "-" + week)) {
                            if (!forumRelations.get(thread_id + "-" + week).contains(indexes.get(commenter_id + ""))) {
                                forumRelations.get(thread_id + "-" + week).
                                        add(indexes.get(commenter_id + ""));
                            }
                        }
                        else{
                            forumRelations.put(thread_id + "-" + week, new ArrayList<>());
                        }
                }
            }
        } catch (Exception e) {
        }
        return forumRelations;
    }

    public static Long getWeek(long first, long actual){
        return (((actual-first)/3600)/24)/7;
    }


}
