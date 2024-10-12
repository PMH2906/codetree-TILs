import java.util.Scanner;
import java.util.ArrayList;

class Pair {
    int x, y;
    
    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isSame(Pair p) {
        return this.x == p.x && this.y == p.y;
    }
}

public class Main {
    public static final int DIR_NUM = 4;
    public static final int MAX_N = 100;
    
    // 변수 선언
    public static int n, m, h, k;
    // 각 칸에 있는 도망자 정보를 관리합니다.
    // 도망자의 방향만 저장하면 충분합니다.
    public static ArrayList<Integer>[][] hiders = new ArrayList[MAX_N][MAX_N];
    public static ArrayList<Integer>[][] nextHiders = new ArrayList[MAX_N][MAX_N];
    public static boolean[][] tree = new boolean[MAX_N][MAX_N];
    
    // 정방향 기준으로
    // 현재 위치에서 술래가 움직여야 할 방향을 관리합니다.
    public static int[][] seekerNextDir = new int[MAX_N][MAX_N];
    // 역방향 기준으로
    // 현재 위치에서 술래가 움직여야 할 방향을 관리합니다.
    public static int[][] seekerRevDir = new int[MAX_N][MAX_N];
    
    // 술래의 현재 위치를 나타냅니다.
    public static Pair seekerPos;
    // 술래가 움직이는 방향이 정방향이면 true / 아니라면 false입니다.
    public static boolean forwardFacing = true;
    
    public static int ans;
    
    // 정중앙으로부터 끝까지 움직이는 경로를 계산해줍니다.
    public static void initializeSeekerPath() {
        // 상우하좌 순서대로 넣어줍니다.
        int[] dx = new int[]{-1, 0, 1,  0};
        int[] dy = new int[]{0 , 1, 0, -1};
    
        // 시작 위치와 방향, 
        // 해당 방향으로 이동할 횟수를 설정합니다. 
        int currX = n / 2, currY = n / 2;
        int moveDir = 0, moveNum = 1;
    
        while(currX > 0 || currY > 0) {
            // moveNum 만큼 이동합니다.
            for(int i = 0; i < moveNum; i++) {
                seekerNextDir[currX][currY] = moveDir;
                currX += dx[moveDir]; currY += dy[moveDir];
                seekerRevDir[currX][currY] = (moveDir < 2) ? (moveDir + 2) : (moveDir - 2);
    
                // 이동하는 도중 (0, 0)으로 오게 되면,
                // 움직이는 것을 종료합니다.
                if(currX == 0 && currY == 0)
                    break;
            }
            
            // 방향을 바꿉니다.
            moveDir = (moveDir + 1) % 4;
            // 만약 현재 방향이 위 혹은 아래가 된 경우에는
            // 특정 방향으로 움직여야 할 횟수를 1 증가시킵니다.
            if(moveDir == 0 || moveDir == 2)
                moveNum++;
        }
    }
    
    // 격자 내에 있는지를 판단합니다.
    public static boolean inRange(int x, int y) {
        return 0 <= x && x < n && 0 <= y && y < n;
    }
    
    public static void hiderMove(int x, int y, int moveDir) {
        // 좌우하상 순서대로 넣어줍니다.
        int[] dx = new int[]{0 , 0, 1, -1};
        int[] dy = new int[]{-1, 1, 0,  0};
    
        int nx = x + dx[moveDir], ny = y + dy[moveDir];
        // Step 1.
        // 만약 격자를 벗어난다면
        // 우선 방향을 틀어줍니다.
        if(!inRange(nx, ny)) {
            // 0 <-> 1 , 2 <-> 3이 되어야 합니다.
            moveDir = (moveDir < 2) ? (1 - moveDir) : (5 - moveDir);
            nx = x + dx[moveDir]; ny = y + dy[moveDir];
        }
        
        // Step 2.
        // 그 다음 위치에 술래가 없다면 움직여줍니다.
        if(!new Pair(nx, ny).isSame(seekerPos))
            nextHiders[nx][ny].add(moveDir);
        // 술래가 있다면 더 움직이지 않습니다.
        else
            nextHiders[x][y].add(moveDir);
    }
    
    public static int distFromSeeker(int x, int y) {
         // 현재 술래의 위치를 불러옵니다.
        int seekerX = seekerPos.x;
        int seekerY = seekerPos.y;
    
        return Math.abs(seekerX - x) + Math.abs(seekerY - y);
    }
    
    public static void hiderMoveAll() {
        // Step 1. next hider를 초기화해줍니다.
        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                nextHiders[i][j] = new ArrayList<>();
        
        // Step 2. hider를 전부 움직여줍니다.
        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++) {
                // 술래와의 거리가 3 이내인 도망자들에 대해서만
                // 움직여줍니다.
                if(distFromSeeker(i, j) <= 3) {
                    for(int k = 0; k < hiders[i][j].size(); k++)
                        hiderMove(i, j, hiders[i][j].get(k));
                }
                // 그렇지 않다면 현재 위치 그대로 넣어줍니다.
                else {
                    for(int k = 0; k < hiders[i][j].size(); k++)
                        nextHiders[i][j].add(hiders[i][j].get(k));
                }
            }
    
        // Step 3. next hider값을 옮겨줍니다.
        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                hiders[i][j] = new ArrayList<>(nextHiders[i][j]);
    }
    
    // 현재 술래가 바라보는 방향을 가져옵니다.
    public static int getSeekerDir() {
        // 현재 술래의 위치를 불러옵니다.
        int x = seekerPos.x;
        int y = seekerPos.y;
    
        // 어느 방향으로 움직여야 하는지에 대한 정보를 가져옵니다.
        int moveDir;
        if(forwardFacing)
            moveDir = seekerNextDir[x][y];
        else
            moveDir = seekerRevDir[x][y];
        
        return moveDir;
    }
    
    public static void checkFacing() {
        // Case 1. 정방향으로 끝에 다다른 경우라면, 방향을 바꿔줍니다.
        if(seekerPos.isSame(new Pair(0, 0)) && forwardFacing)
            forwardFacing = false;
        // Case 2. 역방향으로 끝에 다다른 경우여도, 방향을 바꿔줍니다.
        if(seekerPos.isSame(new Pair(n / 2, n / 2)) && !forwardFacing)
            forwardFacing = true;
    }
    
    public static void seekerMove() {
        int x = seekerPos.x;
        int y = seekerPos.y;
    
        // 상우하좌 순서대로 넣어줍니다.
        int[] dx = new int[]{-1, 0, 1,  0};
        int[] dy = new int[]{0 , 1, 0, -1};
    
        int moveDir = getSeekerDir();
    
        // 술래를 한 칸 움직여줍니다.
        seekerPos = new Pair(x + dx[moveDir], y + dy[moveDir]);
        
        // 끝에 도달했다면 방향을 바꿔줘야 합니다.
        checkFacing();
    }
    
    public static void getScore(int t) {
        // 상우하좌 순서대로 넣어줍니다.
        int[] dx = new int[]{-1, 0, 1,  0};
        int[] dy = new int[]{0 , 1, 0, -1};
    
        // 현재 술래의 위치를 불러옵니다.
        int x = seekerPos.x;
        int y = seekerPos.y;
        
        // 술래의 방향을 불러옵니다.
        int moveDir = getSeekerDir();
        
        // 3칸을 바라봅니다.
        for(int dist = 0; dist < 3; dist++) {
            int nx = x + dist * dx[moveDir], ny = y + dist * dy[moveDir];
            
            // 격자를 벗어나지 않으며 나무가 없는 위치라면 
            // 도망자들을 전부 잡게 됩니다.
            if(inRange(nx, ny) && !tree[nx][ny]) {
                // 해당 위치의 도망자 수 만큼 점수를 얻게 됩니다.
                ans += t * hiders[nx][ny].size();
    
                // 도망자들이 사라지게 됩니다.
                hiders[nx][ny] = new ArrayList<>();
            }
        }
    }
    
    public static void simulate(int t) {
        // 도망자가 움직입니다.
        hiderMoveAll();
    
        // 술래가 움직입니다.
        seekerMove();
        
        // 점수를 얻습니다.
        getScore(t);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // 입력: 
        n = sc.nextInt();
        m = sc.nextInt();
        h = sc.nextInt();
        k = sc.nextInt();

        for(int i = 0; i < n; i++)
            for(int j = 0; j < n; j++)
                hiders[i][j] = new ArrayList<>();

        // 술래 정보를 입력받습니다.
        while(m-- > 0) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            int d = sc.nextInt();
            hiders[x - 1][y - 1].add(d);
        }

        // 나무 정보를 입력받습니다.
        while(h-- > 0) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            tree[x - 1][y - 1] = true;
        }

        // 술래의 처음 위치를 설정합니다.
        seekerPos = new Pair(n / 2, n / 2);

        // 술래잡기 시작 전에
        // 구현상의 편의를 위해
        // 술래 경로 정보를 미리 계산합니다.
        initializeSeekerPath();

        // k번에 걸쳐 술래잡기를 진행합니다.
        for(int t = 1; t <= k; t++)
            simulate(t);
        
        System.out.print(ans);
    }
}