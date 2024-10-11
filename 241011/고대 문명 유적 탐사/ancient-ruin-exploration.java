import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int K, M, ans;
	static int[][] map, deltas= {{1,0},{-1,0},{0,1},{0,-1}};
	static Queue<Integer> num;
	
	public static class DiaInfo implements Comparable<DiaInfo>{
		int x, y;

		public DiaInfo(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo(DiaInfo o) {
			if(this.y==o.y) {
				return Integer.compare(this.x, o.x)*-1;
			}
			return Integer.compare(this.y, o.y);
		}
		
	}
	public static class Rotation implements Comparable<Rotation>{
		int x, y, degree;
		int[][] mapTemp;
		PriorityQueue<DiaInfo> getDiaInfo;
		
		public Rotation(int x, int y, int degree, int[][] map) {
			super();
			this.x = x;
			this.y = y;
			this.degree = degree;
			rotate(map);
			getDia();
		}
		
		
		public void rotate(int[][] map) {
			this.mapTemp=new int[5][5];
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					// 회전 범위면 
					if((r>=this.x&&r<this.x+3)&&(c>=this.y&&c<this.y+3)) {
						if(degree==90) {
							this.mapTemp[r][c]=map[this.x+2-(c-this.y)][this.y+(r-this.x)];
						} else if(degree==180) {
							this.mapTemp[r][c]=map[this.x+2-(r-this.x)][this.y+2-(c-this.y)];
						} else if(degree==270) {
							this.mapTemp[r][c]=map[this.x+(c-this.y)][this.y+2-(r-this.x)];
						}
					}
					// 회전 범위 아니면 
					else this.mapTemp[r][c]=map[r][c];
				}
			}
		}
		
		public void getDia() {
			this.getDiaInfo = new PriorityQueue<>();
			boolean[][] visited=new boolean[5][5];
			
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					if(!visited[r][c]) {
						Queue<int[]> q= new LinkedList<int[]>();
						Queue<int[]> cntQ=new  LinkedList<int[]>();
						
						q.add(new int[] {r,c});
						cntQ.add(new int[] {r,c});
						visited[r][c]=true;
						
						while(q.size()>0) {
							int[] now=q.poll();
							for(int d=0;d<deltas.length;d++) {
								int nx=now[0]+deltas[d][0];
								int ny=now[1]+deltas[d][1];
								
								if(nx<0||nx>=5||ny<0||ny>=5) continue;
								
								if(this.mapTemp[r][c]==this.mapTemp[nx][ny]&&!visited[nx][ny]) {
									q.add(new int[] {nx,ny});
									cntQ.add(new int[] {nx,ny});
									visited[nx][ny]=true;
								}
							}
							
						}
						if(cntQ.size()>=3) {
							while(!cntQ.isEmpty()) {
								int[] now=cntQ.poll();
								this.getDiaInfo.add(new DiaInfo(now[0], now[1]));
							}
						}
					}
				}
			}
		}


		@Override
		public int compareTo(Rotation o) {
			if(this.getDiaInfo.size()==o.getDiaInfo.size()) {
				if(this.degree==o.degree) {
					if(this.y+1==o.y+1) {
						return Integer.compare(this.x+1,o.x+1);
					}
					return Integer.compare(this.y+1,o.y+1);
				}
				return Integer.compare(this.degree,o.degree);
			}
			return Integer.compare(this.getDiaInfo.size(), o.getDiaInfo.size())*-1;
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		K=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
	
		// 초기화
		map=new int[5][5];
		num=new LinkedList<>();
		
		for(int r=0;r<5;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<5;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		tokens=new StringTokenizer(input.readLine());
		while(tokens.hasMoreTokens()) {
			num.add(Integer.parseInt(tokens.nextToken()));
		}
		
		for(int k=0;k<K;k++) {
			ans=0;
			find();
//			System.out.println(k+"턴 ");
//			for(int r=0;r<5;r++) {
//				System.out.println(Arrays.toString(map[r]));
//			}
			getDia();
//			System.out.println(k+"턴 ");
//			for(int r=0;r<5;r++) {
//				System.out.println(Arrays.toString(map[r]));
//			}
			if(ans==0) break;
			output.append(ans+" ");
		}
		System.out.println(output);
	}

	public static void getDia() {	
		while(true) {
			PriorityQueue<DiaInfo> getDiaInfo = new PriorityQueue<>();
			boolean[][] visited=new boolean[5][5];
			
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					if(!visited[r][c]) {
						Queue<int[]> q= new LinkedList<int[]>();
						Queue<int[]> cntQ=new  LinkedList<int[]>();
						
						q.add(new int[] {r,c});
						cntQ.add(new int[] {r,c});
						visited[r][c]=true;
						
						while(q.size()>0) {
							int[] now=q.poll();
							for(int d=0;d<deltas.length;d++) {
								int nx=now[0]+deltas[d][0];
								int ny=now[1]+deltas[d][1];
								
								if(nx<0||nx>=5||ny<0||ny>=5) continue;
								
								if(map[r][c]==map[nx][ny]&&!visited[nx][ny]) {
									q.add(new int[] {nx,ny});
									cntQ.add(new int[] {nx,ny});
									visited[nx][ny]=true;
								}
							}
							
						}
						if(cntQ.size()>=3) {
							while(!cntQ.isEmpty()) {
								int[] now=cntQ.poll();
								getDiaInfo.add(new DiaInfo(now[0], now[1]));
							}
						}
					}
				}
			}
			if(getDiaInfo.size()==0) break;
			fill(getDiaInfo);
		}
	}
	
	private static void find() {
		
		PriorityQueue<Rotation> pq=new PriorityQueue<>();
		
		for(int r=0;r<3;r++) {
			for(int c=0;c<3;c++) {
				pq.add(new Rotation(r, c, 90, map));
				pq.add(new Rotation(r, c, 180, map));
				pq.add(new Rotation(r, c, 270, map));
			}
		}
		
		Rotation rotation =pq.poll();
		
		// 맵 갱신 
		for(int r=0;r<5;r++) {
			for(int c=0;c<5;c++) map[r][c]=rotation.mapTemp[r][c];
		}
		
		// 유물 채우기 
		fill(rotation.getDiaInfo);
	}

	private static void fill(PriorityQueue<DiaInfo> getDiaInfo) {
		ans+=getDiaInfo.size();
		while(!getDiaInfo.isEmpty()) {
			DiaInfo diaInfo=getDiaInfo.poll();
			map[diaInfo.x][diaInfo.y]=num.poll();
		}
	}
}