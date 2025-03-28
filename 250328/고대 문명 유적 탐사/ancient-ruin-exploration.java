package algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

public class 고대문명 {
	
	/**
	 * 1. 회전 
	 * 90, 180, 270 회전
	 * 우선순위 -> 1. 유물 1차 획득 많음, 2. 회전 각도 작음, 3. 열 작음, 4. 행 작음
	 * 
	 * 2. 1차 유물 획득 
	 * 3개 이상 연결된 조각 사라짐
	 * 
	 * 3. 채워짐
	 * 우선순위 1. 열 작음, 2. 행 큼
	 * 
	 * 4. 2차 유물 획득 반복 
	 * 3개 이상 연결된 조각 사라짐
	 * 
	 * 
	 * 총 K번 진행, 유물 획득 못 하면 종료 
	 * 
	 * 출력) 각 턴마다 획득한 유물 가치 총합 
	 * **/
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int K, M, round=0; 
	static int[][] map, temp, visited, deltas= {{-1,0},{1,0},{0,-1},{0,1}};
	static Queue<Integer> nums;
	static PriorityQueue<int[]> fillEmpty;
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		K=Integer.parseInt(tokens.nextToken());
		M=Integer.parseInt(tokens.nextToken());
		
		nums=new LinkedList<Integer>();
		map=new int[5][5];
		temp=new int[5][5];
		visited=new int[5][5];
		fillEmpty=new PriorityQueue<int[]>(new Comparator<int[]>() {

			@Override
			public int compare(int[] o1, int[] o2) {
				if(o1[1]==o2[1]) return Integer.compare(o1[0], o2[0])*-1;
				return Integer.compare(o1[1], o2[1]);
			}
		});
		
		for(int r=0;r<5;r++) {
			tokens=new StringTokenizer(input.readLine());
			for(int c=0;c<5;c++) {
				map[r][c]=Integer.parseInt(tokens.nextToken());
			}
		}
		
		tokens=new StringTokenizer(input.readLine());
		for(int m=0;m<M;m++) {
			nums.add(Integer.parseInt(tokens.nextToken()));
		}
		
		
		/**
		 * 1. 회전 
		 * 90, 180, 270 회전
		 * 우선순위 -> 1. 유물 1차 획득 많음, 2. 회전 각도 작음, 3. 열 작음, 4. 행 작음
		 * 
		 * 2. 1차 유물 획득 
		 * 3개 이상 연결된 조각 사라짐
		 * 
		 * 3. 채워짐
		 * 우선순위 1. 열 작음, 2. 행 큼
		 * 
		 * 4. 2차 유물 획득 반복 
		 * 3개 이상 연결된 조각 사라짐
		 * 
		 * 
		 * 총 K번 진행, 유물 획득 못 하면 종료 
		 * 
		 * 출력) 각 턴마다 획득한 유물 가치 총합 
		 * **/
		
		while(K>0) {
			
			// 회전 선택 
			int maxCnt=Integer.MIN_VALUE;
			int selectDegree=0, selectX=0, selectY=0;
			int cnt=0;
			for(int r=1;r<4;r++) {
				for(int c=1;c<4;c++) {
					for(int degree=90;degree<360;degree+=90) {
						rotate(r,c,degree);
						cnt=getDia1(false,temp);
						
						if(cnt>maxCnt) {
							selectDegree=degree;
							maxCnt=cnt;
							selectX=r;
							selectY=c;
						}
					}	
				}
			}
			
			System.out.println(maxCnt);
			
			// 1차 유물 획득 못 하면 종료 
			 if(maxCnt==0) break;
			
			// 실제 회전 
			rotate(selectX, selectY, selectDegree);
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					map[r][c]=temp[r][c];
				}
			}
			getDia1(true,map);
			for(int r=0;r<5;r++) System.out.println(Arrays.toString(map[r]));
			
			// 2차 유물 획득 
			maxCnt+=getDia2();
			
			output.append(maxCnt+" ");
			K--;
			
			//System.out.println(maxCnt);
		}
		System.out.println(output);
	}

	private static int getDia2() {
		
		int totalCnt=0;
		int cnt=0;
		
		while(true) {
			
			round++;
			cnt=0;
			
			for(int r=0;r<5;r++) {
				for(int c=0;c<5;c++) {
					if(visited[r][c]!=round) {
						visited[r][c]=round;
						cnt=bfs(r,c,true,map);
					}
				}
			}
			
			if(cnt==0) break;
			totalCnt+=cnt;
			
		}
		return totalCnt;
	}

	private static int getDia1(boolean real, int[][] map) {
		
		round++;
		int cnt=0;
		
		for(int r=0;r<5;r++) {
			for(int c=0;c<5;c++) {
				if(visited[r][c]!=round) {
					visited[r][c]=round;
					cnt+=bfs(r,c,real, map);
				}
			}
		}
		
		if(real) {
			while(fillEmpty.size()>0) {
				int[] now=fillEmpty.poll();
				int fill=nums.poll();
				
				map[now[0]][now[1]]=fill;
			}			
		}
		
		return cnt;
	}

	private static int bfs(int r, int c, boolean real, int[][] map) {
		Queue<int[]> empty=new LinkedList<>();
		Queue<int[]> q=new LinkedList<>();
		int num=map[r][c];
		
		q.add(new int[] {r,c});
		empty.add(new int[] {r,c});
		
		while(q.size()>0) {
			
			int[] now=q.poll();
			
			for(int d=0;d<4;d++) {
				int nx=now[0]+deltas[d][0];
				int ny=now[1]+deltas[d][1];
				
				if(nx<0||nx>=5||ny<0||ny>=5) continue;
				
				if(map[nx][ny]==num&&visited[nx][ny]!=round) {
					//System.out.println(map[nx][ny]);
					q.add(new int[] {nx,ny});
					empty.add(new int[] {nx,ny});
					visited[nx][ny]=round;
				}
			}
		}
		
		//System.out.println("num : "+num+" size: "+empty.size());
		
		if(empty.size()<3) return 0;
		
		int size=empty.size();
		
		if(real) {
			while(empty.size()>0) {
				int[] now=empty.poll();
				
				map[now[0]][now[1]]=0;
				fillEmpty.add(new int[] {now[0], now[1]});
			}			
		}
		
		return size;
		
		
	}

	private static void rotate(int x, int y, int degree) {
		
		for(int r=0;r<5;r++) {
			for(int c=0;c<5;c++) {
				
				if((r>=x-1&&r<=x+1)&&(c>=y-1&&c<=y+1)) {
					//System.out.println("x: "+x+" y : "+ y+ " r: "+r +" c: "+c+" " + " nr: "+((x-1)+(2-c))+" nc: "+ ((y-1)+r));
					if(degree==90) {
						temp[r][c]=map[(x-1)+(2-(c-(y-1)))][(y-1)+(r-(x-1))];
					} else if(degree==180) {
						temp[r][c]=map[(x-1)+(2-(r-(x-1)))][(y-1)+(2-(c-(y-1)))];
					} else {
						temp[r][c]=map[(x-1)+(c-(y-1))][(y-1)+(2-(r-(x-1)))];
					}
				}
				else {
					temp[r][c]=map[r][c];
				}
			}
		}
		
//		System.out.println("degree : " + degree);
//		for(int r=0;r<5;r++) System.out.println(Arrays.toString(temp[r]));
	}
}
