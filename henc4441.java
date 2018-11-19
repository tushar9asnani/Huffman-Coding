/* Tushar Asnani    CS610 4441 prp */

import java.io.*;
import java.util.*;

public class henc_4441
{
  public static void main(String tt[])
  {
    if(tt[0] == null)
    {
      System.out.println("Empty Command Line Argument");
      return;
    }
    
    File file = null;
    File outputFile = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fos = null; 
    ObjectOutputStream oos = null;
    
    try{
      
      file = new File(tt[0]);
      fileInputStream = new FileInputStream(file);
      outputFile = new File(tt[0]+".huf");
      fos = new FileOutputStream(outputFile);
      oos = new ObjectOutputStream(fos);
              
      int fileLength = (int)file.length();
      int bytesRead = 0;
      
      byte bytes[]; 
              
      while(bytesRead < fileLength)
      {  
              if((fileLength - bytesRead) > 1024000*2)
                  bytes = new byte[1024000*2];
              else
                  bytes = new byte[fileLength - bytesRead];
                  
              bytesRead += fileInputStream.read(bytes);
              
              ArrayList<Node> nodes = new ArrayList<Node>();
              
              for(byte byteValue: bytes)
              {
                  boolean found = false;
                  
                  for(int i = 0; i < nodes.size(); i++)
                  {
                     if(nodes.get(i).getCharacter() == byteValue)
                     {
                         found = true;
                         Node node = nodes.get(i);
                         node.setFrequency(node.getFrequency()+1);
                         break;
                     } 
                  }
                  
                  if(!found)
                  {
                      Node node = new Node(byteValue, 1);
                      nodes.add(node);
                  }
              }
              
              for(int i = 0; i < nodes.size(); i++)
              {
                     Node node = nodes.get(i);
                     CharInfo charInfo = new CharInfo(node.getCharacter(), node.getFrequency());
                     oos.writeObject(charInfo);
              }
               
              MinHeap minHeap = new MinHeap(nodes.size());
              
              for(int i = 0; i < nodes.size(); i++)
              {
                  minHeap.insert(nodes.get(i));
              }
              
              while (minHeap.size() > 1) 
              {
                  Node left  = minHeap.extract();
                  Node right = minHeap.extract();
                  Node parent = new Node( (byte)'\u0000' , left.getFrequency() + right.getFrequency(), left, right);
                  minHeap.insert(parent);
              }
              
              Node root = minHeap.extract();
              
              nodes.clear();
              
              buildCode(nodes, root, "");
              
              BitSet bitSet = new BitSet();
              int bitcounter = 0;
                             
              for(byte byteValue: bytes)
              { 
                  for(int i = 0; i < nodes.size(); i++)
                  {
                     Node node = nodes.get(i);
                     byte character = node.getCharacter();
                     
                     if(character == byteValue)
                     {
                         String code = node.getCode();
                         for(int j = 0; j < code.length(); j++) 
                         {
                            Character c = code.charAt(j);
                            
                            if(c.equals('1')) 
                            {
                              bitSet.set(bitcounter, true);
                            }
                            else
                            {
                              bitSet.set(bitcounter, false);
                            }
                            
                            bitcounter++;
                         }
                     } 
                  }
               }
               bitSet.set(bitcounter);
               
               Data data = new Data(bitSet.toByteArray());
               
               oos.writeObject(data);
               
       }
       oos.writeObject(null);
      
       file.delete();
    }
    catch(FileNotFoundException fnfException)
    {
      System.out.println("Program terminated; Exception- "+fnfException);
    }
    catch(IOException ioException)
    {
      System.out.println("Program terminated; Exception-"+ioException);
    }
    catch(Exception exception)
    {
      System.out.println("Program terminated; Exception-"+exception);
    }
    finally
    {
      try
      {
        if(fileInputStream != null)fileInputStream.close();
        if(oos != null)oos.close();
        if(fos != null)fos.close();
      }
      catch(IOException ioException)
      {
        System.out.println("Unable to close file input stream due to an exeption "+ioException);
      }
    }
  }
  
  private static void buildCode(ArrayList<Node> nodes, Node node, String code) 
  {
      if (!node.isLeaf()) 
      {
          buildCode(nodes, node.getLeft(),  code+"0");
          buildCode(nodes, node.getRight(), code+"1");
      }
      else 
      {
          node.setCode(code);
          nodes.add(node);
      }
  }
  
}

class Data implements Serializable
{ 
    private byte[]data;
    
    Data(byte[] data)
    {
        this.data = data;
    }
    
    public byte[] getData()
    {
        return this.data;
    }
}

class CharInfo implements Serializable
{
    private byte character;
    private int frequency;
    
    CharInfo(byte character, int frequency)
    {
        this.character = character;
        this.frequency = frequency;
    }
    
    public byte getCharacter()
    {
      return this.character;
    }
    
    public int getFrequency()
    {
      return this.frequency;
    }
}

class Node implements Serializable
{
    private byte character;
    private int frequency;
    private String code;
    private Node left;
    private Node right;
    
    Node(byte character, int frequency)
    {
        this.character = character;
        this.frequency = frequency;
    }
    
    Node(byte character, int frequency, Node left, Node right)
    {
        this.character = character;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }
    
    Node(byte character, String code)
    {
        this.character = character;
        this.code = code;
    }
    
    public boolean isLeaf() 
    {
        assert ((left == null) && (right == null)) || ((left != null) && (right != null));
        return (left == null) && (right == null);
    }
    
    public Node getRight()
    {
        return this.right;
    }
    
    public void setRight(Node right)
    {
        this.right = right;
    }
    
    public Node getLeft()
    {
        return this.left;
    }
    
    public void setLeft(Node left)
    {
        this.left = left;
    }
    
    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }
    
    public int getFrequency()
    {
        return this.frequency;
    }

    public void setCharacter(byte character)
    {
        this.character = character;
    }
    
    public byte getCharacter()
    {
        return this.character;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getCode()
    {
        return this.code;
    }
    
    public String toString()
    {  
        return "Character "+(char)this.character+" Frequency "+this.frequency+" Code "+this.code+"\n";
    }
    
}

class MinHeap
{
    private int size;
    private int currentPointer;
    private Node[] heap;
    
    MinHeap(int size)
    {  
        this.heap = new Node[size+1];
        this.size = size;
    }
    
    public void insert(Node node)
    {
        int insertAt = ++currentPointer;
        heap[insertAt] = node;
        heapify(insertAt);
    }
    
    public void heapify(int position)
    {
        int parent = position/2;
        int current = position;
        
        while(parent > 0 && current > 0 && heap[parent].getFrequency() > heap[current].getFrequency())
        {
            swap(parent, current);
            current = parent;
            parent = parent/2;
        } 
    }
    
    public Node extract()
    {
        Node min = heap[1];
        heap[1] = heap[currentPointer];
        heap[currentPointer] = null;
        sinkDown(1);
        currentPointer--;
        return min;
    }
    
    public void sinkDown(int position)
    {
        int smallest = position;
        int left = 2*position;
        int right = 2*position+1;
        
        if(left < size() && heap[smallest].getFrequency() > heap[left].getFrequency())
        {
            smallest = left;
        }
        
        if(right < size() && heap[smallest].getFrequency() > heap[right].getFrequency())
        {
            smallest = right;
        }
        
        if(smallest != position)
        {
            swap(position, smallest);
            sinkDown(smallest);
        }
    }
    
    public void swap(int first, int second)
    {
        Node temp = heap[first];
        heap[first] = heap[second];
        heap[second] = temp;
    }
    
    public int size()
    {
        return currentPointer;
    }
    
    public void print()
    {    
        int x = currentPointer/2;
        if(currentPointer == 1) 
            x = 1;
        
        for(int i = 1; i <= x; i++)
        {
            System.out.println("Parent "+heap[i]);
            if(2*i <= currentPointer)System.out.println("Left "+heap[2*i]);
            if(2*i+1 <= currentPointer)System.out.println("Right "+heap[2*i+1]+"\n");
        }
    }
}