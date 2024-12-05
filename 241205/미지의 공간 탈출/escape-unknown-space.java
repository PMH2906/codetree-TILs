import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int N,M,F, ans;
	static int[][] map, deltas={{0,1},{0,-1},{1,0},{-1,0}};
	static int[][][] timeMap;
	static Info[] infos;
	static int[] finish, start, startTimeMap, gate;
	
	public static class Info {
		int x, y, dir, v, nextTime;

		public Info(int x, int y, int dir, int v) {
			super();
			this.x = x;
			this.y = y;
			this.dir = dir;
			this.v = v;
			this.nextTime=v;
		}
		
		
	}
	public static void main(String[] args) throws IOException {
		tokens=new StringTokenizer(input.readLine());
		
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		F=Integer.parseInt(tokens.nextToken());
		
		map=new int[N][N];
		timeMap=new int[5][M][M];
		infos=new Info[F];
		finish=new int[2];
		start=new int[2];
		startTimeMap=new int[] {-1,-1};
		gate=new int[2];
		ans=0;

		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
				if(map[r][c]==4) {
					finish[0]=r; finish[1]=c;
				} else if(map[r][c]==3&&startTimeMap[0]==-1) {
					startTimeMap[0]=r; startTimeMap[1]=c;
				} 
			}
		}
		
		for(int d=0;d<5;d++) {
			for(int r=0;r<M;r++) {
				tokens=new StringTokenizer(input.readLine());
				for(int c=0;c<M;c++) {
					timeMap[d][r][c]=Integer.parseInt(tokens.nextToken());
					if(timeMap[d][r][c]==2) {
						start[0]=r; start[1]=c;
					}
				}
			}
		}
		
		for(int f=0;f<F;f++) {
			tokens=new StringTokenizer(input.readLine());
			int x=Integer.parseInt(tokens.nextToken());
			int y=Integer.parseInt(tokens.nextToken());
			int d=Integer.parseInt(tokens.nextToken());
			int v=Integer.parseInt(tokens.nextToken());
			
			infos[f]=new Info(x, y, d, v);
		}
		
		findGate();
		if(!exitTimeMap()) {
			System.out.println(-1);
			return;
		}; 
		
		// System.out.println(ans);
		
		for(Info info:infos) {
			// 시간의 벽 탈출 시간보다 확산 시간이 클 경우 확산 X
			if(info.nextTime>ans) continue;
			
			while(info.nextTime<=ans) {
				if(!spread(info)) break;
			}
		}
		
		if(!exitMap()) {
			System.out.println(-1);
			return;
		}
		
		System.out.println(ans);
		
	}
	
	private static boolean exitMap() {
		
		Queue<int[]> q=new LinkedList<int[]>();
		boolean[][] visited=new boolean[N][N];
		int time=ans;
		
		q.add(new int[] {gate[0], gate[1], ans+1});
		visited[gate[0]][gate[1]]=true;
		
		while(q.size()>0) {
			int[] now=q.poll();
			
			// 확산 
			if(time!=now[2]) {
				time+=1;
				for(Info info:infos) {
					// 시간의 벽 탈출 시간보다 확산 시간이 클 경우 확산 X
					if(info.nextTime==time) spread(info);
				}
			}
			
			// 확산 후 움직일 수 있는지 확인 
			if(map[now[0]][now[1]]==1) continue;
			
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(map[nx][ny]==0&&!visited[nx][ny]) {
					q.add(new int[] {nx, ny, now[2]+1});
					visited[nx][ny]=true;
				} else if(map[nx][ny]==4) {
					ans=now[2]+1;
					return true;
				}
			}
		}
		
		return false;
	}

	private static boolean spread(Info info) {
		
		map[info.x][info.y]=1;
		int nx=info.x+deltas[info.dir][0];
		int ny=info.y+deltas[info.dir][1];
		
		if(nx<0||nx>=N||ny<0||ny>=N) return false;
		if(map[nx][ny]==1||map[nx][ny]==4) return false ;
		
		info.x=nx;
		info.y=ny;
		info.nextTime+=info.v;
		map[info.x][info.y]=1;
		return true;
	}

	private static boolean exitTimeMap() {
		Queue<int[]> q=new LinkedList<int[]>();
		boolean[][][] visited=new boolean[5][M][M];
		q.add(new int[] {start[0], start[1], 4, 0});
		visited[4][start[0]][start[1]]=true;
		
		while(q.size()>0) {
			int[] now=q.poll();
			
			// System.out.println(now[0]+" "+now[1]+" "+ now[2]+" "+now[3]);
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];				
				int ny=now[1]+deltas[d][1];		
				int dir=now[2];
				
				if(now[2]==0) {
					if(nx<0) {
						dir=4;
						nx=(M-1)-ny;
						ny=M-1;
					} else if(ny<0) {
						dir=2;
						ny=M-1;
					} else if(ny>=M) {
						dir=3;
						ny=0;
					}
				} else if(now[2]==1) {
					if(nx<0) {
						dir=4;
						nx=ny;
						ny=0;
					} else if(ny<0) {
						dir=3;
						ny=M-1;
					} else if(ny>=M) {
						dir=2;
						ny=0;
					}
				} else if(now[2]==2) {
					if(nx<0) {
						dir=4;
						nx=M-1;
					} else if(ny<0) {
						dir=1;
						ny=M-1;
					} else if(ny>=M) {
						dir=0;
						ny=0;
					}
				} else if(now[2]==3) {
					if(nx<0) {
						dir=4;
						nx=0;
						ny=(M-1)-ny;
					} else if(ny<0) {
						dir=0;
						ny=M-1;
					} else if(ny>=M) {
						dir=1;
						ny=0;
					}
				} else if(now[2]==4) {
					if(nx<0) {
						dir=3;
						nx=0;
						ny=(M-1)-ny;
					} else if(nx>=M) {
						dir=2;
						nx=0;
					} else if(ny<0) {
						dir=1;
						ny=nx;
						nx=0;
					} else if(ny>=M) {
						dir=0;
						ny=(M-1)-nx;
						nx=0;
					}
				}
	
				if(nx<0||nx>=M||ny<0||ny>=M) continue;
				
				if(timeMap[dir][nx][ny]==0&&!visited[dir][nx][ny]) {
					q.add(new int[] {nx,ny,dir,now[3]+1});
					visited[dir][nx][ny]=true;
				} else if(timeMap[dir][nx][ny]==-1) {
					ans+=now[3]+1;
					return true;
				}
			}
		}
		
		return false;
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
					q.add(new int[] {nx,ny});
					visited[nx][ny]=true;
				} else if(map[nx][ny]==0) {
					gate[0]=nx; gate[1]=ny;
					if(d==0) timeMap[d][M-1][(M-1)-(nx-startTimeMap[0])]=-1;
					else if(d==1) timeMap[d][M-1][nx-startTimeMap[0]]=-1;
					else if(d==2) timeMap[d][M-1][ny-startTimeMap[1]]=-1;
					else if(d==3) timeMap[d][M-1][(M-1)-(ny-startTimeMap[1])]=-1;
					return;
				}
			}
		}
	}

}
