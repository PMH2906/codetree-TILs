import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static StringBuilder output = new StringBuilder();
	static StringTokenizer tokens;
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	static int N, M, K, ans;
	static int[][] deltas = { { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 } };
	static int[][][] map;
	static Ball ball;
	static Pos[] teamInfo;

	static class Pos {
		int firstX, firstY, lastX, lastY;

		public Pos(int firstX, int firstY, int lastX, int lastY) {
			super();
			this.firstX = firstX;
			this.firstY = firstY;
			this.lastX = lastX;
			this.lastY = lastY;
		}

		public Pos() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public String toString() {
			return "Pos [firstX=" + firstX + ", firstY=" + firstY + ", lastX=" + lastX + ", lastY=" + lastY + "]";
		}

	}

	static class Ball {
		int throwDir, x, y, nextDir;

		public Ball(int throwDir, int x, int y, int nextDir) {
			super();
			this.throwDir = throwDir;
			this.x = x;
			this.y = y;
			this.nextDir = nextDir;
		}

	}

	public static void main(String[] args) throws IOException {

		tokens = new StringTokenizer(input.readLine());

		N = Integer.parseInt(tokens.nextToken());
		M = Integer.parseInt(tokens.nextToken());
		K = Integer.parseInt(tokens.nextToken());

		// 초기화
		map = new int[N][N][2];
		ball = new Ball(0, 0, 0, 3);
		teamInfo = new Pos[M];
		ans=0;

		for (int r = 0; r < N; r++) {
			tokens = new StringTokenizer(input.readLine());
			for (int c = 0; c < N; c++) {
				map[r][c][0] = Integer.parseInt(tokens.nextToken());
			}
		}

		// 팀 정보 초기화
		init();
//		for (int r = 0; r < N; r++) {
//			System.out.println();
//			for (int c = 0; c < N; c++) {
//				System.out.print(map[r][c][0]);
//			}
//		}
//		
		for (int k = 1; k <= K; k++) {
//			System.out.println("round : "+k);
			// 이동
			move();
//			for(int i=0;i<M;i++) {
//				System.out.println(teamInfo[i].toString());
//			}
//			for (int r = 0; r < N; r++) {
//				System.out.println();
//				for (int c = 0; c < N; c++) {
//					System.out.print(map[r][c][0]);
//				}
//			}
			// 던지기
			throwBall(k);
//			for(int i=0;i<M;i++) {
//				System.out.println(teamInfo[i].toString());
//			}
//			System.out.println("점수 "+ ans);
		}
		
		output.append(ans);
		System.out.print(output);
	}

	private static void throwBall(int k) {
		int x = ball.x;
		int y = ball.y;
		// 현재 위치 보기
		if(map[x][y][0]!=4&&map[x][y][0]!=0) {
			int nth = count(x,y);
			ans+=nth*nth;
			
			// 던질 공 정보 갱신
			changeBallInfo(k);
//			System.out.println("맞은공 : "+ x+" "+y);
			// 방향 바꾸기 
			int tempX = teamInfo[map[x][y][1]].firstX;
			int tempY = teamInfo[map[x][y][1]].firstY;
			teamInfo[map[x][y][1]].firstX = teamInfo[map[x][y][1]].lastX;
			teamInfo[map[x][y][1]].firstY = teamInfo[map[x][y][1]].lastY;
			teamInfo[map[x][y][1]].lastX = tempX;
			teamInfo[map[x][y][1]].lastY = tempY;
			
			return;
		}
		// 다음 위치 보기
		for(int n=0;n<N-1;n++) {
			int nx = x+deltas[ball.throwDir][0];
			int ny = y+deltas[ball.throwDir][1];
			
			if(map[nx][ny][0]!=4&&map[nx][ny][0]!=0) {
//				System.out.println("맞은공 : "+ nx+" "+ny);
				int nth = count(nx,ny);
				ans+=nth*nth;
				
				// 방향 바꾸기 
				int tempX = teamInfo[map[nx][ny][1]].firstX;
				int tempY = teamInfo[map[nx][ny][1]].firstY;
				teamInfo[map[nx][ny][1]].firstX = teamInfo[map[nx][ny][1]].lastX;
				teamInfo[map[nx][ny][1]].firstY = teamInfo[map[nx][ny][1]].lastY;
				teamInfo[map[nx][ny][1]].lastX = tempX;
				teamInfo[map[nx][ny][1]].lastY = tempY;
				break;
			}
			x=nx;
			y=ny;
		}
		
		// 던질 공 정보 갱신
		changeBallInfo(k);
	}

	private static void changeBallInfo(int k) {
//		System.out.println("round"+k+"ball:"+ball.x+" "+ball.y+" "+ball.nextDir+" "+ball.throwDir);
		if(k%N==0) {
			ball.nextDir=(ball.nextDir+1)%4;
			ball.throwDir=(ball.throwDir+1)%4;
		} else {
			ball.x=ball.x + deltas[ball.nextDir][0];
			ball.y=ball.y + deltas[ball.nextDir][1];
		}
	}

	private static int count(int x, int y) {
		if(map[x][y][0]==1) {
			return 1;
		}
		Queue<int[]> q = new LinkedList<>();
		boolean[][] visited = new boolean[N][N];
		q.add(new int[] { x, y, 0});
	
		while (q.size() > 0) {
			int[] now = q.poll();
			
			for (int d = 0; d < deltas.length; d++) {
				int nx = now[0] + deltas[d][0];
				int ny = now[1] + deltas[d][1];

				if (nx < 0 || nx >= N || ny < 0 || ny >= N)
					continue;
				if(map[nx][ny][0]==1&& map[now[0]][now[1]][0]==2) {
					return now[2]+2;
				}
				if (map[nx][ny][0]==2&&!visited[nx][ny]) {
					q.add(new int[] { nx, ny, now[2]+1});
					visited[nx][ny] = true;
				}
			}
		}
		return 0;
	}

	// 이동
	private static void move() {
		for (int m = 0; m < M; m++) {
			for (int d = 0; d < deltas.length; d++) {
				int nx = teamInfo[m].lastX + deltas[d][0];
				int ny = teamInfo[m].lastY + deltas[d][1];

				if (nx < 0 || nx >= N || ny < 0 || ny >= N)
					continue;

				if (map[nx][ny][0] == 2) {
					map[nx][ny][0] = 3;
					map[teamInfo[m].lastX][teamInfo[m].lastY][0] = 4;
					teamInfo[m].lastX = nx;
					teamInfo[m].lastY = ny;
					break;
				}
			}
			
			for (int d = 0; d < deltas.length; d++) {
				int nx = teamInfo[m].firstX + deltas[d][0];
				int ny = teamInfo[m].firstY + deltas[d][1];

				if (nx < 0 || nx >= N || ny < 0 || ny >= N)
					continue;

				if (map[nx][ny][0] == 4) {
					map[nx][ny][0] = 1;
					map[teamInfo[m].firstX][teamInfo[m].firstY][0] = 2;
					teamInfo[m].firstX = nx;
					teamInfo[m].firstY = ny;
					break;
				}
			}
		}
	}

	private static void init() {
		boolean[][] visited = new boolean[N][N];
		int teamIdx = 0;
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (map[r][c][0] != 0 && !visited[r][c]) {
					visited[r][c] = true;
					bfs(r, c, visited, teamIdx);
					teamIdx++;
				}
			}
		}
//		for (int r = 0; r < N; r++) {
//			System.out.println();
//			for (int c = 0; c < N; c++) {
//				System.out.print(map[r][c][1]);
//			}
//		}
	}

	private static void bfs(int r, int c, boolean[][] visited, int teamIdx) {
		Queue<int[]> q = new LinkedList<>();
		q.add(new int[] { r, c });
		teamInfo[teamIdx] = new Pos();

		while (q.size() > 0) {
			int[] now = q.poll();
			map[now[0]][now[1]][1]=teamIdx;

			if (map[now[0]][now[1]][0] == 1) {
				teamInfo[teamIdx].firstX = now[0];
				teamInfo[teamIdx].firstY = now[1];
			} else if (map[now[0]][now[1]][0] == 3) {
				teamInfo[teamIdx].lastX = now[0];
				teamInfo[teamIdx].lastY = now[1];
			}

			for (int d = 0; d < deltas.length; d++) {
				int nx = now[0] + deltas[d][0];
				int ny = now[1] + deltas[d][1];

				if (nx < 0 || nx >= N || ny < 0 || ny >= N)
					continue;

				if (map[nx][ny][0] != 0 && !visited[nx][ny]) {
					q.add(new int[] { nx, ny });
					visited[nx][ny] = true;
				}
			}
		}

	}

}