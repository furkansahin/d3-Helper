import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by sahinfurkan on 23/10/15.
 */
public class write {
    public static void main(String[] args) throws IOException {

        final int GRAPH_SIZE = 700;
        HashMap<String, Integer> indexes = new HashMap<>();
        HashMap<Integer, String> indexesKey = new HashMap<>();
        int[] lst = new int[10];
        int[] sizes = new int[10];
        String res = "{\"nodes\":[";
        int sum = 0;

        for (int i = 0; i < 10; i++){
            lst[i] = 0;
            sizes[i] = 0;
        }

        // Finds the number of students for each grade interval from 10 to 100 by 10
        for (String line : Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/grades.csv"))){
            String[] tmp = line.split(",");
            int num = Integer.parseInt(tmp[8].substring(0, tmp[8].indexOf(".")));
            if (num >= 10) {
                lst[(num-10) / 10] += 1;
                sum++;
            }
        }

        System.out.println(sum);

        // Finds the number of students that should be included in 200 nodes for each grade interval
        for (int i = 0; i < 10; i++){
            lst[i] = (int)((((float)lst[i])/sum)*GRAPH_SIZE);
        }
/*
        while (sum2 < 200){
            for (int i = 0; sum2 < 200 && i < 10; i++){
                lst[lst.length - i - 1] += 1;
                sum2++;
            }
        }
        for (int i = 0; i < 10; i++){
            System.out.println(lst[i]);
        }
        System.out.println(sum2);
  */
        int a = 0;
        for (String line : Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/grades.csv"))){
            String node = "";
            String[] tmp = line.split(",");
            int num = Integer.parseInt(tmp[8].substring(0, tmp[8].indexOf(".")));

            if (num >= 10 && sizes[(num-10)/10] < lst[(num-10)/10]) {

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
        HashMap<Integer, HashMap<Integer, Boolean>> forumRelations = getForumRelations(indexes/*, forumThreads*/ );
        int b = 0;
        res += "],\"links\":[";

        HashMap<String, Edge> edges = getEdges(forumRelations);

        for (Edge edge : edges.values()){
            res += "{\"source\":" + edge.source + ", \"target\":" + edge.target + ", \"value\":" + (edge.value*1000) + ", \"week\":" + 1 + "},\n";
        }

        res = res.substring(0, res.length()-2);
        res += "]}";


        PrintWriter writer = new PrintWriter("allData.json", "UTF-8");
        writer.println(res);
        writer.close();
    }

    public static HashMap<String, Edge> getEdges(HashMap<Integer, HashMap<Integer, Boolean>> forumRelations){
        HashMap<String, Edge> edges = new HashMap<>();
        int size = 0;
        for (HashMap<Integer, Boolean> users: forumRelations.values()){
            if(users.size() > 1){
                size++;
            }
        }
        for (HashMap<Integer, Boolean> users: forumRelations.values()){
            Set<Integer> keys = users.keySet();
            Integer[] arr = keys.toArray(new Integer[keys.size()]);
            for (int i = 0; i < arr.length; i++){
                for (int j = i + 1; j < arr.length; j++){
                    if (edges.containsKey(arr[i] + "" + arr[j])){
                        edges.get(arr[i] + "" + arr[j]).value += ((float)1)/ size;
                    }
                    else if(edges.containsKey(arr[j] + "" + arr[i])){
                        edges.get(arr[j] + "" + arr[i]).value += ((float)1)/ size;
                    }
                    else{
                        edges.put(arr[i] + "" + arr[j], new Edge(arr[i], arr[j], ((float)1)/ size));
                    }
                }
            }
        }
        return edges;
    }
    public static class Edge{
        public float value;
        public int source;
        public int target;

        public Edge(int source, int target, float value){
            this.source = source;
            this.target = target;
            this.value = value;
        }

    }
    /*
    public static HashMap<Integer, Pair<Integer, String>> getForumThreads(HashMap<Integer, String> indexes){
        HashMap<Integer, Pair<Integer, String>> forumThreads = new HashMap<Integer, Pair<Integer, String>>();
        try{
            List<String> allThreads = Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/forum_threads.csv"));
            for (String count: allThreads){
                String[] tmp = count.split(",");
                if (indexes.containsKey(Integer.parseInt(tmp[2]))) {
                    forumThreads.put(Integer.parseInt(tmp[0]), new Pair(Integer.parseInt(tmp[2]), tmp[17]));
                }
            }
        }
        catch(Exception e){
        }
        return forumThreads;
    }
*/
    public static HashMap<Integer, HashMap<Integer, Boolean>> getForumRelations(HashMap<String, Integer> indexes/*,HashMap<Integer, Pair<Integer, String>> threads, */) {
        HashMap<Integer, HashMap<Integer, Boolean>> forumRelations = new HashMap<Integer, HashMap<Integer, Boolean>>();
        try {
            List<String> allPosts = Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/forum_posts.csv"));
            for (String post : allPosts) {
                String[] tmp = post.split(",");
                int thread_id = Integer.parseInt(tmp[1]);
                int commenter_id = Integer.parseInt(tmp[2]);
    //            if (threads.containsKey(thread_id)) {
    //                int owner_id = threads.get(thread_id).getLeft();
                    if (indexes.containsKey(commenter_id + "")) {
  //                      if (commenter_id != owner_id) {
                            if (forumRelations.containsKey(thread_id)) {
                                if (!forumRelations.get(thread_id).containsKey(commenter_id)) {
                                    forumRelations.get(thread_id).put(indexes.get(commenter_id + ""), false);
                                }
                            }
                            else{
                                forumRelations.put(thread_id, new HashMap<>());
                                forumRelations.get(thread_id).put(indexes.get(commenter_id+""), false);
                            }
   //                     }
//                    }
                }
            }
        } catch (Exception e) {
        }
        return forumRelations;
    }


}
