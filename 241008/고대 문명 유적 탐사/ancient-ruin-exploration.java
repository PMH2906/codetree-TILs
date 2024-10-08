import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
	
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int K, M, ans=0;
	static int[][] map;
	static int[][] deltas= {{0,1},{0,-1},{1,0},{-1,0}};
	static Queue<Integer> nextNum = new LinkedList<>();
	
	public static class GetItemInfo implements Comparable<GetItemInfo>{

		// 현재 각도와 중심 좌표로 회전 후 map 상태 
		int[][] map=new int[5][5];
		// 최종 1차 유물 획득 큐
		PriorityQueue<Point> getItem=new PriorityQueue<>();
		int centerX,centerY,angle;
		
		public GetItemInfo(int centerX, int centerY, int angle, int[][] map) {
			this.centerX=centerX;
			this.centerY=centerY;
			this.angle=angle;
			this.rotate(map);
			this.getItem();
		}

		/**
		 * 유물 1차 회득 중 회전 목표의 우선순위가 가장 높은 경우 찾기 
		 * (1) 유물 1차 획득 가치를 최대화
		 * (2) 회전한 각도가 가장 작은 방법
		 * (3) 회전 중심 좌표의 열이 가장 작은 구간
		 * (4) 열이 같다면 행이 가장 작은 구간
		 * **/
		@Override
		public int compareTo(GetItemInfo o) {
			if(this.getItem.size()==o.getItem.size()) {
				if(this.angle==o.angle) {
					if(this.centerY==o.centerY) {
						return Integer.compare(this.centerX, o.centerX);
					} 
					return Integer.compare(this.centerY, o.centerY);
				}
				return Integer.compare(this.angle, o.angle);
			}
			return Integer.compare(this.getItem.size(), o.getItem.size())*-1;
		}
		
		/**
		 * 중심좌표와 각도로 map 움직임 
		 * **/
		public void rotate(int[][] map) {
	
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					if(r>this.centerX+1||r<this.centerX-1||c>this.centerY+1||c<this.centerY-1) {
						this.map[r][c]=map[r][c];
					} else {
						/**
						 * 주의!!!!!!!! (r,c)-(중심좌표X-1, 중심좌표Y-1)로 하여 시작점을 (0,0)으로 만들고 생각하기  
						 * 주의!!!!!!!! 또한, 기본 시작점이 (중심좌표X-1, 중심좌표Y-1)이므로 해당 좌표 더해주기 
						 * 따라서 (중심좌표X-1, 중심좌표Y-1) + 각 회전에 따른 (r,c)-(중심좌표X-1, 중심좌표Y-1)
						 * 
						 * 원래 (0,0)으로 생각하면 90도 :newMap[r][c]=map[크기-1-c][r]
						 * 원래 (0,0)으로 생각하면 180도 :newMap[r][c]=map[크기-1-r][크기-1-c]
						 * 원래 (0,0)으로 생각하면 270도 :newMap[r][c]=map[c][크기-1-r]
						 * **/
						if(angle==90) {
							this.map[r][c]=map[3-1-(c-(centerY-1))+centerX-1][(r-(centerX-1))+centerY-1];
						} else if(angle==180) {
							this.map[r][c]=map[3-1-(r-(centerX-1))+centerX-1][3-1-(c-(centerY-1))+centerY-1];
						} else {
							this.map[r][c]=map[(c-(centerY-1))+centerX-1][3-1-(r-(centerX-1))+centerY-1];
						}	
					}
				}
			}			
		}
		
		/**
		 * 회전시킨 map 기준으로 1차 유물 획득 찾기  
		 * **/
		public void getItem() {
			boolean[][] visited=new boolean[5][5];
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					if(!visited[r][c]) {
						
						// 현재 탐색하는 숫자의 유물 위치 저장 
						PriorityQueue<Point> getItemPerNum=new PriorityQueue<>();		
						// 현재 탐색하는 숫자의 다음 탐색할 유물 위치 삽입 
						Queue<int[]> q= new LinkedList<>();
						
						q.add(new int[] {r,c});
						visited[r][c]=true;
						getItemPerNum.add(new Point(r,c));
						
						while(q.size()>0) {
							int[] now=q.poll();
							
							for(int d=0;d<deltas.length;d++) {
								int nx=now[0]+deltas[d][0];
								int ny=now[1]+deltas[d][1];
								
								if(nx<0||nx>=5||ny<0||ny>=5) continue;
								
								if(!visited[nx][ny]&&this.map[nx][ny]==this.map[now[0]][now[1]]) {
									q.add(new int[] {nx,ny});
									getItemPerNum.add(new Point(nx,ny));
									visited[nx][ny]=true;
								}
							}
						}
						
						// 현재 탐색하는 숫자의 유물 갯수가 3개 이상이면 최종 1차 유물 획득 큐에 넣기  
						if(getItemPerNum.size()>=3) getItem.addAll(getItemPerNum);
					}
				}
			}
		}
	}
	
	public static class Point implements Comparable<Point>{
		int x, y;


		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo(Point o) {
			
			if(this.y==o.y) {
				return Integer.compare(this.x, o.x)*-1;
			}
			else return Integer.compare(this.y, o.y);
		}	
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		K=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		map=new int[5][5];
		
		for(int r=0;r<5;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<5;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		tokens=new StringTokenizer(input.readLine());
		while(tokens.hasMoreTokens()) {
			nextNum.add(Integer.parseInt(tokens.nextToken()));
		}
		
		for(int k=0;k<K;k++) {
			
			ans=0;
			
			// 유물 1차 획득 
			// 유물 획득하지 못 하면 종료 
			if(!getFirstItem()) break;
			
			getSecondItem();
			
			output.append(ans+" ");
		}
		
		System.out.println(output);
		
	}

	/**
	 * 유물 연쇄 획득 
	 * **/
	private static void getSecondItem() {
		
		while(true) {
			boolean[][] visited=new boolean[5][5];
			PriorityQueue<Point> getItem=new PriorityQueue<>();	
			
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					if(!visited[r][c]) {
						PriorityQueue<Point> getItemPerNum=new PriorityQueue<>();				
						Queue<int[]> q= new LinkedList<>();
						
						q.add(new int[] {r,c});
						visited[r][c]=true;
						getItemPerNum.add(new Point(r,c));
						
						while(q.size()>0) {
							int[] now=q.poll();
							
							for(int d=0;d<deltas.length;d++) {
								int nx=now[0]+deltas[d][0];
								int ny=now[1]+deltas[d][1];
								
								if(nx<0||nx>=5||ny<0||ny>=5) continue;
								
								if(!visited[nx][ny]&&map[nx][ny]==map[now[0]][now[1]]) {
									q.add(new int[] {nx,ny});
									getItemPerNum.add(new Point(nx,ny));
									visited[nx][ny]=true;
								}
							}
						}
						
						if(getItemPerNum.size()>=3) getItem.addAll(getItemPerNum);
					}
				}
			}
			
			// 획득할 유물 없으면 종료 
			if(getItem.size()==0) break;
			ans+=getItem.size();
			fillItem(getItem);
		}
	}
		
	/**
	 * 유물 1차 획득 
	 * **/
	public static boolean getFirstItem() {
		
		// 1차 유물 회득 중 회전 목표의 우선순위가 가장 높은 경우 찾기 
		// (1) 유물 1차 획득 가치를 최대화
		// (2) 회전한 각도가 가장 작은 방법
		// (3) 회전 중심 좌표의 열이 가장 작은 구간
		// (4) 열이 같다면 행이 가장 작은 구간
		PriorityQueue<GetItemInfo> getItemInfo=new PriorityQueue<>();
		for(int r=1;r<4;r++) {
			for(int c=1;c<4;c++) {
				for(int angle=90;angle<=270;angle+=90) {
					getItemInfo.add(new GetItemInfo(r, c, angle, map));
				}
			}
		}
		
		// 1차 유물 회득 중 회전 목표의 우선순위가 가장 높은 경우 획득 
		GetItemInfo getMaxItemInfo=getItemInfo.poll();
		
		// 1차 유물 획득하지 못 하면 false
		if(getMaxItemInfo.getItem.size()==0) return false;
		
		// 1차 유물 획득 후 배열 돌리기 
		for(int r=0;r<5;r++) {
			for(int c=0;c<5;c++) {
				map[r][c]=getMaxItemInfo.map[r][c];
			}
		}
		
		ans+=getMaxItemInfo.getItem.size();
		fillItem(getMaxItemInfo.getItem);
		
		return true;
	}
	
	// 사라진 유물에 숫자 넣기 
	public static void fillItem(PriorityQueue<Point> points) {
		
		while(points.size()>0) {
			Point now=points.poll();
			map[now.x][now.y]=nextNum.poll();
		}
	}
}