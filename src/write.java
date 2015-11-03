import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by sahinfurkan on 23/10/15.
 */
public class write {
    public static void main(String[] args) throws IOException {

        HashMap<String, Integer> indexes = new HashMap<>();
        HashMap<Integer, String> indexesKey = new HashMap<>();
        int[] lst = new int[11];
        int[] sizes = new int[11];
        ArrayList<String> sts = new ArrayList<>();
        String[] stsFull = new String[50000];
        String res = "{\"nodes\":[";
        int sum = 0;
        int a = 0;

        for (int i = 0; i < 50000; ++i){
            stsFull[i] = "";
        }
        for (int i = 0; i < 11; i++){
            lst[i] = 0;
            sizes[i] = 0;
        }

        for (String line : Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/grades.csv"))){
            String[] tmp = line.split(",");
            int num = Integer.parseInt(tmp[8].substring(0, tmp[8].indexOf(".")));
            lst[num/10] = lst[num/10] + 1;
            sum++;
        }

        for (int i = 0; i < 11; i++){
            lst[i] = lst[i]/(sum/200);
            System.out.println(lst[i]);

        }

        for (String line : Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/grades.csv"))){
            String node = "";
            String[] tmp = line.split(",");
            int num = Integer.parseInt(tmp[8].substring(0, tmp[8].indexOf(".")));

            if (sizes[num/10] < lst[num/10]) {

                sizes[num/10]++;

//                System.out.println(a);
                indexes.put(tmp[0], a++);
                indexesKey.put(a-1, tmp[0]);
                node += "{\"id\":\"" + tmp[0] + "\",\n";
                node += "\"name\":\"\",\n";
                node += "\"genre\":\"" + tmp[9] + "\",\n";
                node += "\"price\":\"" + tmp[8] + "\"},\n";

                stsFull[a-1] = node;

                //      }
            }
        }
        for (int i = 0; i < stsFull.length; i++){
            res += stsFull[i];
        }
        System.out.println("HEY//////////////////////////////////////////////////////////////");
        res = res.substring(0, res.length()-3);
        res += "}";
        System.gc();

        HashMap<Integer, Pair<Integer, String>> forumThreads = getForumThreads(indexesKey);
        HashMap<Integer, HashMap<Integer, Short>> forumRelations = getForumRelations(forumThreads, indexesKey);
        int b = 0;
        res += "],";
//        res += mockEdges();

        /*
        for (Pair<Integer, String> pair : forumThreads.values()){

            int owner = pair.getLeft();
            HashMap<Integer, Short> map = forumRelations.get(owner);
            if (map != null){
                Set<Integer> myKeyz = map.keySet();
                List<Integer> myKeys = new ArrayList<Integer>(myKeyz);

                for (int i = 0; i < map.size(); ++i ){
                    if (indexes.containsKey(myKeys.get(i) + "")) {
                        res += "{\"source\":" + myKeys.get(i) + ",\"target\":" + owner + ",\"value\":" + map.get(myKeys.get(i)) + "},\n";
                        System.out.println(b++);

                    }
                }
            }
        }


        res = res.substring(0, res.length()-3);
   */     res += "]}";


        PrintWriter writer = new PrintWriter("allData.json", "UTF-8");
        writer.println(res);
        writer.close();
    }

    public static String mockEdges(){
        String links = "\"links\":[";

        for (int i = 0; i < 5; i++){
            for (int j = i * 40 + 1; j < i*40 + 40; ++j){
                links += "{\"source\":" + (i*40) + ",\"target\":" + j + ",\"value\":" + Math.random() * 5 + "},\n";
            }
        }
        return links.substring(0, links.length()-3);
    }
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

    public static HashMap<Integer, HashMap<Integer, Short>> getForumRelations(HashMap<Integer, Pair<Integer, String>> threads, HashMap<Integer, String> indexes) {
        HashMap<Integer, HashMap<Integer, Short>> forumRelations = new HashMap<Integer, HashMap<Integer, Short>>();
        try {
            List<String> allPosts = Files.readAllLines(Paths.get("/Users/sahinfurkan/Desktop/forum_posts.csv"));
            for (String post : allPosts) {
                String[] tmp = post.split(",");
                int thread_id = Integer.parseInt(tmp[1]);
                int commenter_id = Integer.parseInt(tmp[2]);
                if (threads.containsKey(thread_id)) {
                    int owner_id = threads.get(thread_id).getLeft();
                    if (indexes.containsKey(commenter_id) && indexes.containsKey(owner_id)) {
                        if (commenter_id != owner_id) {
                            if (forumRelations.containsKey(owner_id)) {
                                if (forumRelations.get(owner_id).containsKey(commenter_id)) {
                                    forumRelations.get(owner_id).put(commenter_id, (short) 2);
                                } else {
                                    forumRelations.get(owner_id).put(commenter_id, (short) 1);
                                }
                            } else if (forumRelations.containsKey(commenter_id)) {
                                if (forumRelations.get(commenter_id).containsKey(owner_id)) {
                                    forumRelations.get(commenter_id).put(owner_id, (short) 2);
                                } else {

                                    forumRelations.get(commenter_id).put(owner_id, (short) 1);
                                }
                            } else {
                                forumRelations.put(owner_id, new HashMap<>());
                                forumRelations.get(owner_id).put(commenter_id, (short) 1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return forumRelations;
    }


}
