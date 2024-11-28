import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int N, M, F, ans;
	static int[][] map, timeMap, deltas= {{0,1},{0,-1},{1,0},{-1,0}};
	static int[] finish, startTimeMap, gate, start;
	static Effect[] effect;
	
	public static class Effect {
		
		int x, y, dir, v, nowV;

		public Effect(int x, int y, int dir, int v) {
			super();
			this.x = x;
			this.y = y;
			this.dir = dir;
			this.v = v;
			this.nowV=v;
		}
	}
	
	public static void main(String[] args) throws IOException {
		tokens=new StringTokenizer(input.readLine());
		
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		F=Integer.parseInt(tokens.nextToken());
		
		map=new int[N][N];
		timeMap=new int[3*M][3*M]; // 시간의 벽 정육면체 전개도 
		for(int r=0;r<3*M;r++) Arrays.fill(timeMap[r],-1);
		startTimeMap=new int[]{-1,-1}; // 미지의 공간에서 시간의 벽이 시작하는 위치(3의 첫번째 위치) 
		effect=new Effect[F]; // 이상현상 정보 
		gate=new int[2]; // 미지의 공간과 시간의 벽이 이어지는 출구 위치 
		start=new int[2]; // 타임머신 위치 
		ans=-1; // 답 
		
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
				
				// 시간의 벽 시작 위치 
				if(map[r][c]==3&&startTimeMap[0]==-1) {
					startTimeMap[0]=r;
					startTimeMap[1]=c;
				}
			}
		}
		
		for(int z=0;z<5;z++) {
			for(int r=0;r<M;r++) {
				tokens=new StringTokenizer(input.readLine());
				for(int c=0;c<M;c++) {
					int n=Integer.parseInt(tokens.nextToken());
					
					// 타임머신 위치 초기화 
					if(n==2) {
						start[0]=M+r;
						start[1]=M+c;
					}
					
					// 시간의 벽 정육면체 전개도 초기화
					// 동, 서, 남, 북에 따라 전처리 
					if(z==0) { // 동 
						timeMap[2*M-1-c][2*M+r]=n;
					} else if(z==1) { // 서 
						timeMap[M+c][M-1-r]=n;
					} else if(z==2) { // 남 
						timeMap[2*M+r][M+c]=n;
					} else if(z==3) { // 북 
						timeMap[M-1-r][2*M-1-c]=n;
					} else if(z==4) { // 윗면 
						timeMap[M+r][M+c]=n;
					} 
				}
			}
		}
		
		for(int f=0;f<F;f++) {
			tokens=new StringTokenizer(input.readLine());
			int x=Integer.parseInt(tokens.nextToken());
			int y=Integer.parseInt(tokens.nextToken());
			int dir=Integer.parseInt(tokens.nextToken());
			int v=Integer.parseInt(tokens.nextToken());
			effect[f]=new Effect(x,y,dir,v);
		}
		
		// 시간의 벽과 미지의 공간 바닫으로 이어진 출구 찾고 timeMap에 셋팅 
		findGate();

		// 시간의 벽에서 탈출 
		int time=exitTimeMap();
		
		// 시간의 벽 탈출까지 확산된 이상현상 계산
		for(int f=0;f<F;f++) {
			while(effect[f].nowV<=time) {
				effect(effect[f]);
			}
		}
		
		// 미지의 공간에서 이동 
		exitMap(time);

		output.append(ans);
		System.out.print(output);
	}

	private static void exitMap(int time) {
		
		Queue<int[]> q=new LinkedList<>();
		q.add(new int[] {gate[0], gate[1],time+1});
		boolean[][] visited=new boolean[N][N];
		visited[gate[0]][gate[1]]=true;
		
		while(q.size()>0) {
			int[] now=q.poll();

			// 이상 현상 확산 
			for(int f=0;f<F;f++) {
				if(effect[f].nowV==now[2]) {
					effect(effect[f]);
				}
			}
			
			if(map[now[0]][now[1]]==1) continue;
			
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(map[nx][ny]==0&&!visited[nx][ny]) {
					q.add(new int[] {nx,ny,now[2]+1});
					visited[nx][ny]=true;
				} else { // 탈출구 도착 시 종료 
					if(map[nx][ny]==4) {
						ans=now[2]+1;
						return;
					}
				}
			}
		}
	}

	private static void effect(Effect effect) {
		
		// 초기 확산을 위해 코드 추가 
		// V초에는 이상현상의 초기 위치와 방향에 따른 다음 위치, 총 2개의 위치에 이상현상이 확산된다. 
		map[effect.x][effect.y]=1;
		
		int nx=effect.x+deltas[effect.dir][0];
		int ny=effect.y+deltas[effect.dir][1];
	
		effect.nowV+=effect.v;
		
		if(nx<0||nx>=N||ny<0||ny>=N) return;
		if(map[nx][ny]==1||map[nx][ny]==4) return;
		
		// 확산 
		map[nx][ny]=1;
		
		// 다음 확산을 위해 이동 
		effect.x=nx;
		effect.y=ny;
	}

	private static int exitTimeMap() {
		Queue<int[]> q=new LinkedList<int[]>();
		boolean[][] visited=new boolean[3*M][3*M];
		q.add(new int[] {start[0], start[1], 0});
		visited[start[0]][start[1]]=true;
		
		while(q.size()>0) {
			int[] now=q.poll();
			
			// 시간의 벽과 미지의 공간이 이어지는 출구 도착 시 종료 
			if(timeMap[now[0]][now[1]]==3) {
				return now[2];
			}
			
			for(int d=0;d<4;d++) { // 동서남북 
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=3*M||ny<0||ny>=3*M) continue;
				
				// 시간의 벽 정육면체 전개도에서 넘어갈 때 방향에 따라 아래와 같이 다음 위치가 수정된다. 
				if(nx>=0&&nx<=M-1&&ny>=0&&ny<=M-1) {
					if(d==1) {
						ny=nx;
						nx=M;
					} else if(d==3) {
						nx=ny;
						ny=M;
					}
				} else if(nx>=0&&nx<=M-1&&ny>=2*M&&ny<=3*M-1) {
					if(d==0) {
						ny=(3*M-1)-nx;
						nx=M;
					} else if(d==3) {
						nx=(3*M-1)-ny;
						ny=2*M-1;
					}
				} else if(nx>=2*M&&nx<=3*M-1&&ny>=0&&ny<=M-1) {
					if(d==1) {
						ny=(3*M-1)-nx;
						nx=2*M-1;
					} else if(d==2) {
						nx=2*M+ny;
						ny=M;
					}
				} else if(nx>=2*M&&nx<=3*M-1&&ny>=2*M&&ny<=3*M-1) {
					if(d==0) {
						ny=nx;
						nx=2*M-1;
					} else if(d==2) {
						nx=ny;
						ny=2*M-1;
					}
				}
				
				if(!visited[nx][ny]&&(timeMap[nx][ny]==0||timeMap[nx][ny]==3)) {
					q.add(new int[] {nx, ny, now[2]+1});
					visited[nx][ny]=true;
				}
			}
		}
		return 0;
	}

	private static void findGate() {
		Queue<int[]> q=new LinkedList<int[]>();
		boolean[][] visited=new boolean[N][N];
		q.add(new int[] {startTimeMap[0], startTimeMap[1]});
		visited[startTimeMap[0]][startTimeMap[1]]=true;
		
		while(q.size()>0) {
			int[] now=q.poll();
			
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(map[nx][ny]==3&&!visited[nx][ny]) {
					q.add(new int[] {nx, ny});
					visited[nx][ny]=true;
				} else if(map[nx][ny]==0&&!visited[nx][ny]) {
					
					// 미지의 공간과 시간의 벽 사이의 출구 
					gate[0]=nx; gate[1]=ny;
					
					// 출구의 위치에 따라 시간의 벽 정육면체 전개도에 위치할 gate 전처리
					// 시간의 벽 정육면체 전개도에서 gate는 3으로 표시된다. 즉, 시간의 벽에서는 2->3으로 도달하는 최단 거리를 구하면 된다. 
					if(d==0) { // 동
						timeMap[M+(gate[0]-startTimeMap[0])][3*M-1]=3;
					} else if(d==1) { // 서 
						timeMap[M+(gate[0]-startTimeMap[0])][0]=3;
					} else if(d==2) { // 남 
						timeMap[3*M-1][M+(gate[1]-startTimeMap[1])]=3;
					} else if(d==3) {
						timeMap[0][M+(gate[1]-startTimeMap[1])]=3;
					}
					
					return;
				}
			}
		}
	}

}
