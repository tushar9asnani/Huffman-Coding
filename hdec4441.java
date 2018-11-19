/* Tushar Asnani    CS610 4441 prp */


import java.io.*;

import java.util.*;



public class hdec_4441


{
    public static void main(String tt[])
   
 {
        if(tt[0] == null)
      
  {
          System.out.println("Empty Command Line Argument");
        
  return;
       
 }
        
   
     String inputFileName = tt[0];
  
      File file = null;
     
   FileInputStream fileInputStream = null;
   
     ObjectInputStream ois = null;
      
  String outputFileName = inputFileName.replace(".huf","");
     
        
        ArrayList<Node> nodes = new ArrayList<Node>();
     
   Data data = null;
        
       
 try
        {
        
        
    file = new File(inputFileName);
       
     fileInputStream = new FileInputStream(file);
        
    ois = new ObjectInputStream(fileInputStream);
         
   FileOutputStream fos = new FileOutputStream(new File(outputFileName));
     
    
            while(true)
           
 {       
                  
  nodes.clear();
                   
      
              Object obj = ois.readObject();
    
                
                    if(obj == null)
           
           break;
                    
                   
 while(!(obj instanceof Data)) 
                  
  {
                        CharInfo charInfo = (CharInfo) obj;
                        Node node1 = new Node(charInfo.getCharacter(), charInfo.getFrequency());
                        nodes.add(node1);
                    
                        obj = ois.readObject();
                    }
                    
                    data = (Data) obj;
            
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
                  
                  BitSet bitSet = BitSet.valueOf(data.getData());
                   
                   for( int i = 0; i < bitSet.length()-1; )
                   {
                       Node node = root;
                       
                       while(!node.isLeaf())
                       {
                         
                             if(bitSet.get(i))
                             {
                                 node = node.getRight();
                             }
                             else
                             {
                                 node = node.getLeft();
                             }
                             
                             i++;
                       }
                       
                       fos.write((char)node.getCharacter());
                   }
                   
               }     
            file.delete();
        }
        catch(FileNotFoundException fnfException)
        {
          System.out.println("Program terminated; Exception- "+fnfException);
        }
        catch(IOException ioException)
        {
          System.out.println("Program terminated; Exception- "+ioException);
        }
        catch(Exception exception)
        {
          System.out.println("Program terminated; Exception- "+exception);
        }
        finally
        {
          try
          {
            if(fileInputStream != null)fileInputStream.close();
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

