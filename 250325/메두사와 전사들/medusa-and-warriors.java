package algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

// 메두사 : 집 -> 공원 (최단 경로)
// 전사 : 메두사한테 이동(최단 경로)
// 0 도로, 1 도로 X

// 1. 메두사 이동
//		최단 거리, 상 하 좌 우 우선 이동 
//		이동한 곳의 전사는 사라짐

// => Q로 돌린다음, 같은 최단 경로면 첫 스타트의 상 하 좌 우 비교해서 갱신. x,y,startDir,dist

// 2. 메두사 시선
//		메두사를 많이 볼 수 있는 방향, 상 하 좌 우
// 		돌로 변함. 현재 턴에서 움직일 수 X, 다음 턴 풀림 

// => 상 하 좌 우 함수 생성. return cnt. cnt가 크면 seen[][] 갱신. 
// => 

// 3. 전사 이동
//		메두사를 향해 2칸 이동, 메두사 시야 이동 X
//		1번째 : 거리를 줄일 수 잇는 방향, 상 하 좌 우
// 		2번째 : 거리룰 줄일 수 있는 방향, 좌 우 상 하


// 4. 전사 공격
//		메두사와 같은 칸의 전사는 사라짐 

// 매턴 마다 -> 전사가 이동한 거리 합, 메두사로 인해 돌이 된 전사 수(시야에 잡힌 전사 수), 메두사를 공격한 전사수 
// N 50, M 300
public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output;
	static int N, M;
	static int[] start, finish;
	static int[][] map, visited, deltas= {{-1,0},{1,0},{0,-1},{0,1}}; // 상하좌우
	static Warrior[] warrior;
	static int round;
	static boolean[][] seen, temp;
	
	static public class Warrior {
		int x, y;

		public Warrior(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		
		
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		
		start=new int[2];
		finish=new int[2];
		map=new int[N][N];
		warrior=new Warrior[M];
		visited=new int[N][N];
		seen=new boolean[N][N];
		temp=new boolean[N][N];
		
		tokens=new StringTokenizer(input.readLine());
		start[0]=Integer.parseInt(tokens.nextToken());
		start[1]=Integer.parseInt(tokens.nextToken());
		finish[0]=Integer.parseInt(tokens.nextToken());
		finish[1]=Integer.parseInt(tokens.nextToken());
		
		for(int m=0;m<M;m++) {
			tokens=new StringTokenizer(input.readLine());
			
			warrior[m]=new Warrior(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));
		}
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		while(true) {
			
			// 메두사 이동 
			if(!moveMonster()) {
				output.append(-1);
				break;
			};
			
			if(start[0]==finish[0]&&finish[1]==start[1]) {
				output.append(0);
				break;
			}
			
			// 시선
			// 2500*4=10,000
			int maxCnt=Integer.MIN_VALUE; // 출력 
			int cnt=0; 
			
			cnt=upOrDown(0); // 상
			if(cnt>maxCnt) {
				maxCnt=cnt;
				for(int r=0;r<N;r++) {
					for(int c=0;c<N;c++) seen[r][c]=temp[r][c];
				}
			}
			cnt=upOrDown(1); // 하
			if(cnt>maxCnt) {
				maxCnt=cnt;
				for(int r=0;r<N;r++) {
					for(int c=0;c<N;c++) seen[r][c]=temp[r][c];
				}
			}
		}
		
		System.out.println(output);
		
	}

	private static int upOrDown(int dir) {
		
		// 시선 채우기 
		// 50*50 = 2,500
		int depth=1;
		int x=start[0]+deltas[dir][0];
		int y=start[1]+deltas[dir][1];
		
		while(x>=0&&x<N) {
			int startY=Math.max(0, y-depth);
			int endY=Math.min(N-1, y+depth);
			
			for(int ny=startY;ny<=endY;ny++) {
				temp[x][ny]=true;
			}
			x+=deltas[dir][0];
			depth++;
		}
		
		// 300
		for(Warrior w : warrior) {
			// 다시 false로 바꿔주기 
			if(temp[w.x][w.y]) {
				
				depth=1;
				x=w.x+deltas[dir][0];
				y=w.y+deltas[dir][1];
								
				// 왼쪽
				if(start[1]>w.y) {
					while(x>=0&&x<N) {
						int startY=Math.max(0, y-depth);
						int endY=y;
						
						for(int ny=startY;ny<=endY;ny++) {
							temp[x][ny]=false;
						}
						x+=deltas[dir][0];
						depth++;
					}
				}
				
				// 중간
				if(start[1]==w.y) {
					while(x>=0&&x<N) {
						temp[x][y]=false;
						
						x+=deltas[dir][0];
					}
				}
				
				// 오른쪽
				if(start[1]>w.y) {
					while(x>=0&&x<N) {
						int startY=y;
						int endY=Math.min(N-1, y+depth);
						
						for(int ny=startY;ny<=endY;ny++) {
							temp[x][ny]=false;
						}
						x+=deltas[dir][0];
						depth++;
					}
				}
			}
		}
		
		// 300
		// 갯수 세기 
		int cnt=0;
		for(Warrior w : warrior) {
			if(temp[w.x][w.y]) cnt++;
		}
		
		return cnt;
	}

	private static boolean moveMonster() {
		round++;
		Queue<int[]> q=new LinkedList<>();
		
		q.add(new int[] {start[0], start[1],-1,0});
		visited[start[0]][start[1]]=round;
		
		while(q.size()>0) {
			
			int[] now=q.poll();
			
			if(now[0]==finish[0]&&now[1]==finish[1]) {
				// 이동 
				start[0]+=deltas[now[2]][0];
				start[1]+=deltas[now[2]][1];
				return true;
			}
				
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(visited[nx][ny]!=round&&map[nx][ny]==0) {
					
					if(now[2]==-1) q.add(new int[] {nx, ny, d, now[3]+1});
					else q.add(new int[] {nx, ny, now[2], now[3]+1});
					
					visited[nx][ny]=round;
				}
			}
			
		}
		
		return false;
	}
	
	
}
