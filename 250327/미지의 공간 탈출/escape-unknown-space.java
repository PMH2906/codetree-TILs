import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 2차원 평면, 미지의 공간 N * N
 * 정육면체, 시간의 벽 M
 * 
 * 위, 동 서 남 북 
 * 1 장애물 0 빈 공간, 0만 이동 가능 
 * 
 * 시간의 벽 -> 타임머신 2로 추가
 * 미지의 공간 -> 시간의 벽 위치 3(한칸은 빈칸), 탈출구 4
 * 
 * 이상현상 r,c, v의 배수마다 방향 d로 확산
 * -> 1과 탈출구가 아닌 빈공간(0)으로 만 이동, 없으면 확산 중지
 * -> d : 동 서 남 북 
 * -> 이상현상 확산되고 타임머신 이동 
 * 
 * 출력 : 2-> 4로 오는 최소 시간, 불가능하면 -1
 *
 * 입력 : 
 * N(20),M(10),F(10)
 * 위, 동 서 남 북
 * F개의 시간 현상 r, c, d, v(1000)
 * **/
public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int N, M, F, round, ans;
	static int[][] map,visitedMap, deltas= {{0,1},{0,-1},{1,0},{-1,0}};
	static int[][][] timeMap, visitedTimeMap;
	static int[] start, finish, startTimeMap, finishTimeMap;
	static Effect[] effects;
	static PriorityQueue<int[]> effectTime;
	
	static class Effect {
		int x, y, d, v, nth;

		public Effect(int x, int y, int d, int v) {
			super();
			this.x = x;
			this.y = y;
			this.d = d;
			this.v = v;
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		
		N=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		F=Integer.parseInt(tokens.nextToken());
		
		map=new int[N][N];
		timeMap=new int[5][M][M];
		start=new int[2];  
		finish=new int[2];
		startTimeMap=new int[2];
		finishTimeMap=new int[2];
		effects=new Effect[F];
		visitedMap=new int[N][N];
		visitedTimeMap=new int[5][M][M];
		round=0;
		ans=0;
		effectTime=new PriorityQueue<>(new Comparator<int[]>() { // 시간, index

			@Override
			public int compare(int[] o1, int[] o2) {
				// TODO Auto-generated method stub
				return Integer.compare(o1[0], o2[0]);
			}
		});
		
		boolean check=false;
		for(int r=0;r<N;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<N;c++) {
				int n=Integer.parseInt(tokens.nextToken());
				
				if(n==4) {
					finish[0]=r; finish[1]=c;
				} 
				else if(n==3&!check) {
					startTimeMap[0]=r; startTimeMap[1]=c;
					check=true;
				}
				
				map[r][c]=n;
			}
		}
		
		for(int z=1;z<=5;z++) {
			for(int r=0;r<M;r++) {
				tokens=new StringTokenizer(input.readLine());
				for(int c=0;c<M;c++) {
					int n=Integer.parseInt(tokens.nextToken());
					
					if(n==2) {
						start[0]=r; start[1]=c;
					} 
					timeMap[z%5][r][c]=n;
				}
			}
		}
		
		for(int f=0;f<F;f++) {
			tokens=new StringTokenizer(input.readLine());
			effects[f]=new Effect(Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));
		}
		
		// 시간의 벽의 탈출구를 미지의 공간에서 찾기
		// 시간의 벽 3은 미지의 공간과 이어지는 곳
		findGate();
		
		// 시간의 벽 탈출 시간
		ans=exitTimeMap();
		
//		System.out.println(ans);
		
		if(ans==-1) {
			output.append(-1);
			System.out.println(output);
			return;
		}
		
		// 탈출구까지 온 시각 
		ans+=1;
		
		// 탈출구까지 온 시각까지의 이상 현상 이동
		effect();
		
//		for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
//		System.out.println();
		
		// 미지의 공간 탈출 
		ans=exitMap();
		if(ans==-1) {
			output.append(-1);
			System.out.println(output);
			return;
		}
		output.append(ans);
		System.out.println(output);
	}

	private static int exitMap() {
		round++;
		Queue<int[]> q=new LinkedList<>();
		
		q.add(new int[] {finishTimeMap[0], finishTimeMap[1], ans});
		visitedMap[finishTimeMap[0]][finishTimeMap[1]]=round;
		
		while(q.size()>0) {
			int[] now=q.poll();
			
			// 지금 꺼낸 시각이랑 다르면 이상현상 확산
			// 같으면 확산할 필요 없음 
			if(ans!=now[2]) {
				ans=now[2];
				while(effectTime.size()>0&&ans>=effectTime.peek()[0]) {
					int[] temp=effectTime.poll();
					
					int nx=effects[temp[1]].x+deltas[effects[temp[1]].d][0];
					int ny=effects[temp[1]].y+deltas[effects[temp[1]].d][1];
					
					if(nx<0||nx>=N||ny<0||ny>=N||map[nx][ny]==1||map[nx][ny]==4) continue;
					
					map[nx][ny]=1;
					effects[temp[1]].x=nx;
					effects[temp[1]].y=ny;
					effects[temp[1]].nth+=1;
					effectTime.add(new int[] {effects[temp[1]].v*(effects[temp[1]].nth+1),temp[1]});
				}
			
//				System.out.println("확산"+ans);
//				for(int r=0;r<N;r++) System.out.println(Arrays.toString(map[r]));
//				System.out.println();
			}
			
			// BFS 탐색 중 지금 현재 시각에 1이 되면 해당 칸은 탐색 금지 
			if(map[now[0]][now[1]]==1) continue;
			
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(map[nx][ny]==0&&visitedMap[nx][ny]!=round) {
					q.add(new int[] {nx, ny, now[2]+1});
					visitedMap[nx][ny]=round;
//					System.out.println("끝 x: "+nx+" y: "+ny+" ans :"+(now[2]+1));
				} else if(map[nx][ny]==4&&visitedMap[nx][ny]!=round) {
//					System.out.println("끝 x: "+nx+" y: "+ny+" ans :"+(now[2]+1));
					return now[2]+1;
				}
				
			}
		}
		return -1;
	}

	private static void effect() {
		loop : for(int i=0;i<effects.length;i++) {
			map[effects[i].x][effects[i].y]=1;
			if(ans>=effects[i].v) {
				for(int j=1;j<=ans/effects[i].v;j++) {
					int nx=effects[i].x+deltas[effects[i].d][0];
					int ny=effects[i].y+deltas[effects[i].d][1];
					
					if(nx<0||nx>=N||ny<0||ny>=N||map[nx][ny]==1||map[nx][ny]==4) continue loop;
					
					map[nx][ny]=1;
					effects[i].x=nx;
					effects[i].y=ny;
					effects[i].nth+=1;
				}
			}
			
			// 다음 확산 시간 우선순위 큐에 넣기
			// System.out.println(effects[i].v*(effects[i].nth+1));
			effectTime.add(new int[] {effects[i].v*(effects[i].nth+1),i});
		}	
	}

	private static int exitTimeMap() {
		round++;
		Queue<int[]> q=new LinkedList<>();
		
		q.add(new int[] {0, start[0], start[1], 0}); // 면, x, y, time
		visitedTimeMap[0][start[0]][start[1]]=round;
		
		while(q.size()>0) {
			int[] now=q.poll();
			//System.out.println("차원 : "+now[0]+"x : "+now[1]+"y : "+now[2] + " dist : " + now[3]);
			
			// 위 동 서 남 북 
			for(int d=0;d<4;d++) {
				int nx=now[1]+deltas[d][0];
				int ny=now[2]+deltas[d][1];
				int nz=now[0];
				
				//System.out.println("다음 차원 : "+nz+"x : "+nx+"y : "+ny);
				
				// 다른 면으로 넘어갔을 때 
				if(nx<0||nx>=M||ny<0||ny>=M) {
					// System.out.println("넘어감");
					if(now[0]==0) { // 위
						if(d==0) { // 동
							ny=(M-1)-nx; nx=0; 
							nz=1;
						} else if(d==1) { // 서
							ny=nx; nx=0;
							nz=2;
						} else if(d==2) { // 남
							nx=0;
							nz=3;
						} else { // 북
							ny=(M-1)-ny; nx=0;
							nz=4;
						}
					} else if(now[0]==1) { // 동
						if(d==0) { // 동
							ny=0; 
							nz=4;
						} else if(d==1) { // 서
							ny=M-1;
							nz=3;
						} else if(d==3) { // 북
							nx=(M-1)-ny; ny=M-1;
							nz=0;
						}
					} else if(now[0]==2) { // 서
						if(d==0) { // 동
							ny=0; 
							nz=3;
						} else if(d==1) { // 서
							ny=M-1;
							nz=4;
						} else if(d==3) { // 북
							nx=ny; ny=0;
							nz=0;
						}
					} else if(now[0]==3) { // 남
						if(d==0) { // 동
							ny=0; 
							nz=1;
						} else if(d==1) { // 서
							ny=M-1;
							nz=2;
						} else if(d==3) { // 북
							nx=M-1;
							nz=0;
						}
					} else { // 북
						if(d==0) { // 동
							ny=0;
							nz=2;
						} else if(d==1) { // 서
							ny=M-1; 
							nz=1;
						} else if(d==3) { // 북
							nx=0; ny=(M-1)-ny;
							nz=0;
						}
					}
				}
				

				// 동 서 남 북 면에서 남 방향으로 가면 pass
				if(nx<0||nx>=M||ny<0||ny>=M) continue;
				if(timeMap[nz][nx][ny]==1||visitedTimeMap[nz][nx][ny]==round) continue;
				
				if(timeMap[nz][nx][ny]==3) return now[3]+1;
				
				//System.out.println("넘어간 차원 : "+nz+"x : "+nx+"y : "+ny);
				q.add(new int[] {nz, nx, ny, now[3]+1});
				visitedTimeMap[nz][nx][ny]=round;
				
			}
		}
		
		return -1;
	}

	private static void findGate() {
		round++;
		Queue<int[]> q=new LinkedList<>();
		
		q.add(startTimeMap);
		visitedMap[startTimeMap[0]][startTimeMap[1]]=round;
		
		while(q.size()>0) {
			int[] now=q.poll();
			
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=N||ny<0||ny>=N) continue;
				
				if(map[nx][ny]==3&&visitedMap[nx][ny]!=round) {
					q.add(new int[] {nx,ny});
					visitedMap[nx][ny]=round;
				}
				
				// 위 동 서 남 북 
				if(map[nx][ny]==0&&visitedMap[nx][ny]!=round) {
					if(d==0) { // 동
						timeMap[1][M-1][(M-1)-(nx-startTimeMap[0])]=3;
					} else if(d==1) { // 서
						timeMap[2][M-1][nx-startTimeMap[0]]=3;
					} else if(d==2) { // 남
						timeMap[3][M-1][ny-startTimeMap[1]]=3;
					} else { // 북
						timeMap[4][M-1][(M-1)-(ny-startTimeMap[1])]=3;
					}
					
					finishTimeMap[0]=nx;
					finishTimeMap[1]=ny;
					return;
				}
			}
			
		}
		return;
	}
}
