import java.util.*;
import java.io.*;

// from http://rosettacode.org/wiki/Huffman_coding#Java

abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree(int freq) { frequency = freq; }
 
    // compares on the frequency
    public int compareTo(HuffmanTree tree) {
        return frequency - tree.frequency;
    }
}
 
class HuffmanLeaf extends HuffmanTree {
    public final char value; // the character this leaf represents
 
    public HuffmanLeaf(int freq, char val) {
        super(freq);
        value = val;
    }
}
 
class HuffmanNode extends HuffmanTree {
    public final HuffmanTree left, right; // subtrees
 
    public HuffmanNode(HuffmanTree l, HuffmanTree r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}
 
public class HuffmanCode {
    public static HashMap<Character, String> encodedPairings = new HashMap<Character, String>();
    public static HashMap<String, String> encodedPairings2 = new HashMap<String, String>();
    public static StringBuffer decodedSB = new StringBuffer();
    // input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(ArrayList<AlphaChar> freqCharArray) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        for (int i = 0; i < freqCharArray.size(); i++)
            if (freqCharArray.get(i).freq > 0){
                //trees.offer(new HuffmanLeaf(charFreqs[i], (char)i));
                //freqCharArray
                trees.offer(new HuffmanLeaf(freqCharArray.get(i).freq, freqCharArray.get(i).letter));
            }
 
        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();
 
            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
    }
 
    public static void printCodes(HuffmanTree tree, StringBuffer prefix) {
        assert tree != null;


        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
 
            // print out character, frequency, and code for this leaf (which is just the prefix)
            System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
            encodedPairings.put(leaf.value, prefix.toString());
 
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }

    public static void printCodes2(HuffmanTree tree, HashMap<Character, String> doubleCharMap, StringBuffer prefix) {
        assert tree != null;


        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
 
            // print out character, frequency, and code for this leaf (which is just the prefix)

            //System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
            System.out.println(doubleCharMap.get(leaf.value) + "\t" + leaf.frequency + "\t" + prefix);
            encodedPairings2.put(doubleCharMap.get(leaf.value), prefix.toString());
 
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            // traverse left
            prefix.append('0');
            printCodes2(node.left, doubleCharMap, prefix);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes2(node.right, doubleCharMap, prefix);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }

    //decode(tree, newTree, toDecode, 0, decBw);
    public static void decode(HuffmanTree tree, HuffmanTree newTree, int[] code, int index, BufferedWriter decBw, HashMap<Character, String> doubleCharMap, Boolean one) throws IOException{

        assert tree != null;
        //System.out.println("\n\nbeginning code[index] = " + code[index]);
        if (tree instanceof HuffmanLeaf){
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
            //decodedPairings.(code, leaf.value);
            decodedSB.append(leaf.value);
            //System.out.println("final leaf == "+leaf.value);
            if (one){
                decBw.write(leaf.value);
            }
            else{
                decBw.write(doubleCharMap.get(leaf.value));
            }
            //encBw.write(huffman.encodedPairings.get(charToEncode[i]));

            // check if not the end of array
            if (code.length != index){
                //++index;
                HuffmanNode node = (HuffmanNode)newTree;
                if (code[index] == 0) {
                    decode(node.left, newTree, code, ++index, decBw, doubleCharMap, one);
                }
                else {
                    decode(node.right, newTree, code, ++index, decBw, doubleCharMap, one);

                }
            }
            // ++index
            // if (code[index] == 0) {decode(node.left, code, ++index);}
            //  else {decode(node.right, code, ++index);}
        }
        else if (tree instanceof HuffmanNode){
            HuffmanNode node = (HuffmanNode)tree;

            //System.out.println("before if code[index] = " + index);
            if (code[index] == 0){
                //System.out.println("code[index] = " + code[index]);
                decode(node.left, newTree, code, ++index, decBw, doubleCharMap, one);
            }
            else {
                //System.out.println("code[index] = " + code[index]);
                decode(node.right, newTree, code, ++index, decBw, doubleCharMap, one);

            }
        }
        //if (idex == code.length)
    }
 
    // public static void main(String[] args) {
    //     String test = "this is an example for huffman encoding";
 
    //     // we will assume that all our characters will have
    //     // code less than 256, for simplicity
    //     int[] charFreqs = new int[256];
    //     // read each character and record the frequencies
    //     for (char c : test.toCharArray())
    //         charFreqs[c]++;
 
    //     // build tree
    //     //*****change this to receive an array of our character frequencies
    //     HuffmanTree tree = buildTree(charFreqs);
    //     //freqCharArray
    //     //HuffmanTree tree = buildTree(freqCharArray);
 
    //     // print out results
    //     System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
    //     printCodes(tree, new StringBuffer());
    // }
}