import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;
/*
 * 코드트리_빵
 */
public class Main {
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static int N, M, T;
    static int[][] board;
    static Point[] camps;
    static Person[] persons;
    static int[] dr = { -1, 0, 0, 1 }; // dr, dc(상좌우하 좌표)
    static int[] dc = { 0, -1, 1, 0 };

    /*
     * Point(좌표 정보를 담을 클래스)
     */
    static class Point {
        int r, c; // r, c(좌표의 위치)

        public Point(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    /*
     * Person(사람 정보를 담을 클래스)
     */
    static class Person {
        int r, c; // r, c(사람의 위치)
        Point store; // store(가고 싶은 편의점)
        Point camp; // camp(편의점과 가장 가까이 있는 베이스 캠프)
        boolean isArrived; // isArrived(도착 여부)

        public Person(int r, int c, Point store) {
            this.r = r;
            this.c = c;
            this.store = store;
            isArrived = false;
        }
    }

    /*
     * 편의점 이동 준비
     */
    static void init(StringTokenizer st) throws IOException {
        N = Integer.parseInt(st.nextToken()); // N(격자의 크기)
        M = Integer.parseInt(st.nextToken()); // M(사람의 수)

        board = new int[N][N]; // board(격자의 정보)
        camps = new Point[N * N - M + 1]; // camps(베이스 캠프 저장 배열)
        int campId = 0;
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                int num = Integer.parseInt(st.nextToken());
                if (num == 1) { // 1이라면 베이스 캠프가 위치한 곳이므로
                    camps[campId++] = new Point(i, j); // 베이스 캠프를 저장
                    board[i][j] = num;
                }
            }
        }

        persons = new Person[M + 1]; // persons(사람 저장 배열)
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            Point store = new Point(r, c); // 편의점 저장
            Person person = new Person(0, 0, store); // 사람 저장
            persons[i] = person;
        }
    }

    /*
     * 편의점 이동
     */
    static void move() {
        T = 0; // T(편의점에 도착하는 시간)
        while (true) {
            T++; // 시간 증가
            for (int i = 0; i < M; i++) { // 순서 1
                Person person = persons[i];
                if (!person.isArrived && i + 1 < T) { // 사람의 순서가 T보다 작다면 가고 싶은 편의점 방향으로 이동
                    int direction = selectLocation(person); // direction(이동 방향)
                    person.r = person.r + dr[direction];
                    person.c = person.c + dc[direction];
                }
            }
            for (int i = 0; i < M; i++) { // 순서 2
                Person person = persons[i];
                if (!person.isArrived && i + 1 < T) {
                    if (person.r == person.store.r && person.c == person.store.c) { // 도착한 편의점은 지나갈 수 없도록 함
                        board[person.store.r][person.store.c] = -1;
                        person.isArrived = true;
                    }
                }
            }
            for (int i = 0; i < M; i++) { // 순서 3
                Person person = persons[i];
                if (T == i + 1) { // 사람의 순서가 T와 같다면 편의점과 가장 가까운 위치에 있는 베이스 캠프로 이동
                    Point camp = selectCamp(person.store); // 편의점과 가장 가까이 있는 베이스 캠프를 선택
                    person.camp = camp;
                    person.r = person.camp.r; // 베이스 캠프로 이동
                    person.c = person.camp.c;
                    board[person.r][person.c] = -1; // 도착한 베이스 캠프는 지나갈 수 없도록 함
                }
            }
            if (isOver()) { // 모든 사람이 도착했다면 종료
                break;
            }
        }
    }

    /*
     * 편의점과 가장 가까이 있는 베이스 캠프 선택
     */
    static Point selectCamp(Point store) {
        Point selectedCamp = null; // selectedCamp(최단 거리에 있는 베이스 캠프)
        int minDistance = Integer.MAX_VALUE; // minDistance(최단 거리)

        for (int i = 0; i < camps.length; i++) { // BFS 상좌우하 탐색을 통해 가장 가까운 위치에 있는 베이스 캠프를 찾아냄
            if (camps[i] == null) {
                break;
            }
            if (board[camps[i].r][camps[i].c] == -1) {
                continue; // 이미 사용한 베이스 캠프라면 넘어감
            }

            Point camp = camps[i];
            Queue<int[]> queue = new LinkedList<>();
            queue.add(new int[] { camp.r, camp.c, 0 });
            boolean[][] visited = new boolean[N][N];
            visited[camp.r][camp.c] = true;

            while (!queue.isEmpty()) {
                int[] now = queue.poll();
                int distance = now[2];
                for (int d = 0; d < 4; d++) {
                    int nr = now[0] + dr[d];
                    int nc = now[1] + dc[d];
                    if (nr == store.r && nc == store.c) {
                        if (distance < minDistance) {
                            minDistance = distance; // distance가 minDistance보다 작으면 베이스 캠프 갱신
                            selectedCamp = camp;
                        } else if (distance == minDistance) { // distance가 minDistance와 같으면
                            if (camp.r < selectedCamp.r || (camp.r == selectedCamp.r && camp.c < selectedCamp.c)) {
                                selectedCamp = camp; // distance가 minDistance와 같으면 행이 작은, 열이 작은 베이스 캠프 순으로 갱신
                            }
                        }
                        continue;
                    }
                    if (nr < 0 || nr >= N || nc < 0 || nc >= N || board[nr][nc] == -1 || visited[nr][nc]) {
                        continue;
                    }
                    visited[nr][nc] = true;
                    queue.add(new int[] { nr, nc, distance + 1 });
                }
            }
        }
        return selectedCamp;
    }

    /*
     * 편의점 이동 방향 찾기
     */
    static int selectLocation(Person person) {
        Queue<int[]> queue = new LinkedList<>(); // BFS 상좌우하 탐색을 통해 최단 거리로 이동하는 방향 찾기
        queue.add(new int[] { person.r, person.c, -1 });
        boolean[][] visited = new boolean[N][N];
        visited[person.r][person.c] = true;

        while (!queue.isEmpty()) {
            int[] now = queue.poll();
            if (now[0] == person.store.r && now[1] == person.store.c) {
                return now[2]; // 가장 처음 이동한 방향을 반환
            }
            for (int d = 0; d < 4; d++) {
                int nr = now[0] + dr[d];
                int nc = now[1] + dc[d];
                if (nr < 0 || nr >= N || nc < 0 || nc >= N || board[nr][nc] == -1 || visited[nr][nc]) {
                    continue;
                }
                visited[nr][nc] = true;
                if (now[2] == -1) {
                    queue.add(new int[] { nr, nc, d });
                } else {
                    queue.add(new int[] { nr, nc, now[2] });
                }
            }
        }
        return -1;
    }
    
    /*
     * 모든 사람 도착 여부
     */
    static boolean isOver() {
        for (int i = 0; i < M; i++) {
            if (!persons[i].isArrived) {
                return false;
            }
        }
        return true;
    }

    /*
     * 편의점 이동 시간 출력
     */
    static void print() {
        System.out.println(T);
    }

    public static void main(String[] args) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        init(st);
        move();
        print();
    }
}