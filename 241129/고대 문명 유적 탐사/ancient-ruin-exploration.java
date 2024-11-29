import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static int K, M, value;
	static Queue<Integer> nextDia;
	static int[][] map, deltas= {{1,0},{-1,0},{0,1},{0,-1}};
	
	public static class LotationInfo implements Comparable<LotationInfo>{
		int x, y, degree;
		int[][] temp, newTemp, newMap; // 회전할 3*3 원본 map, 회전할 3*3 새로운 map, 회전한 3*3을 포함한 전체 5*5 map
		PriorityQueue<Point> getDia=new PriorityQueue<>(); // 회전 후 획득한 유적 
		
		public LotationInfo(int x, int y, int degree) {
			super();
			this.x = x;
			this.y = y;
			this.degree = degree;
			temp=new int[3][3];
			newTemp=new int[3][3];
			newMap=new int[5][5];
			for(int r=x;r<x+3;r++) {
				for(int c=y;c<y+3;c++) {
					this.temp[r-x][c-y]=map[r][c];
				}
			}
			// 회전 
			this.lotate();
			// 유물 획득 
			getDia(this.getDia, this.newMap);
		}
		
		public void lotate() {
			
			for(int r=0;r<3;r++) {
				for(int c=0;c<3;c++) {
					if(degree==90) {
						this.newTemp[r][c]=this.temp[2-c][r];
					} else if(degree==180) {
						this.newTemp[r][c]=this.temp[2-r][2-c];
					} else if(degree==270) {
						this.newTemp[r][c]=this.temp[c][2-r];
					}
				}
			}
			
			// 회전한 3*3을 포함한 전체 5*5 map 셋팅 
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					if(r>=x&&r<=x+2&&c>=y&&c<=y+2) this.newMap[r][c]=this.newTemp[r-x][c-y];
					else this.newMap[r][c]=map[r][c];
				}
			}
		}

		@Override
		public int compareTo(LotationInfo o) {
			if(this.getDia.size()==o.getDia.size()) {
				if(this.degree==o.degree) {
					if(this.y==o.y) {
						return Integer.compare(this.x, o.x);
					}
					return Integer.compare(this.y, o.y);
				}
				return Integer.compare(this.degree, o.degree);
			}
			return Integer.compare(this.getDia.size(), o.getDia.size())*-1;
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
			if(this.y==o.y) return Integer.compare(this.x, o.x)*-1;
			return Integer.compare(this.y, o.y);
		}
		
	}
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		
		K=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		
		nextDia=new LinkedList<>();
		map=new int[5][5];
		
		for(int r=0;r<5;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<5;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		tokens=new StringTokenizer(input.readLine());
		for(int m=0;m<M;m++) nextDia.add(Integer.parseInt(tokens.nextToken()));
		
		for(int k=0;k<K;k++) {
			value=0;
			
			// 탐사 진행
			findLotation();
			
			// 연쇄 유물 획득 
			findAgainGetDia();
			
			if(value==0) break;
			output.append(value+" ");
		}
		
		System.out.print(output);
	}
	private static void findAgainGetDia() {
		PriorityQueue<Point> getDia=new PriorityQueue<>();
		while(true) {
			// 유물 획득 
			getDia(getDia, map);
			
			// 획득한 유물이 없을 때 종료 
			if(getDia.isEmpty()) break; 
			
			// 획득한 유물
			value+=getDia.size();
			// 유물 채우기 
			fillDia(getDia);
		}
	}
	private static void getDia(PriorityQueue<Point> getDia, int[][] map) {

		boolean[][] visited=new boolean[5][5];
		
		for(int r=0;r<5;r++) {
			for(int c=0;c<5;c++) {
				if(visited[r][c]) continue;
				Queue<int[]> q=new LinkedList<int[]>();
				PriorityQueue<Point> getDiaTemp=new PriorityQueue<>();
				q.add(new int[] {r,c});
				getDiaTemp.add(new Point(r,c));
				visited[r][c]=true;
				
				while(q.size()>0) {
					int[] now=q.poll();
					
					for(int d=0;d<4;d++) {
						int nx=now[0]+deltas[d][0];
						int ny=now[1]+deltas[d][1];
						
						if(nx<0||nx>=5||ny<0||ny>=5) continue;
						
						if(map[now[0]][now[1]]==map[nx][ny]&&!visited[nx][ny]) {
							q.add(new int[] {nx,ny});
							getDiaTemp.add(new Point(nx, ny));
							visited[nx][ny]=true;
						}
					}
				}
				// 크기가 3인 유물의 위치만 getDia에 삽입 
				if(getDiaTemp.size()>=3) getDia.addAll(getDiaTemp);
				
			}
		}
	}

	private static void findLotation() {
		PriorityQueue<LotationInfo> pq=new PriorityQueue<>();
		
		for(int r=0;r<3;r++) {
			for(int c=0;c<3;c++) {
				for(int d=90;d<=270;d+=90) {
					pq.add(new LotationInfo(r, c, d));
				}
			}
		}
		
		// 1차 유물 획득 
		LotationInfo selectLotationInfo=pq.poll();
		if(selectLotationInfo.getDia.size()==0) return;
		
		// 획득한 유물
		value+=selectLotationInfo.getDia.size();
		
		// 새로운 유적지로 바꾸기 
		for(int r=0;r<5;r++) {
			for(int c=0;c<5;c++) {
				map[r][c]=selectLotationInfo.newMap[r][c];
			}
		}
		
		// 유물 채우기 
		fillDia(selectLotationInfo.getDia);
	}
	
	// 유물 채우기 
	private static void fillDia(PriorityQueue<Point> getDia) {
		
		while(getDia.size()>0) {
			Point point=getDia.poll();
			int newNum=nextDia.poll();
			
			map[point.x][point.y]=newNum;
		}
	}
}
