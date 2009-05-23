package edu.umd.cloud9.io;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.junit.Test;

import edu.umd.cloud9.util.KeyValuePair;
import edu.umd.cloud9.util.SequenceFileUtils;


public class ArrayListOfIntsWritableTest {

	@Test
	public void testReadWrite() throws IOException{
		ArrayListOfIntsWritable arr = new ArrayListOfIntsWritable();
		arr.add(0,1);
		arr.add(1,3);
		arr.add(2,5);
		arr.add(3,7);
		
		FileSystem fs;
		SequenceFile.Writer w;
		Configuration conf = new Configuration();

		try {
			fs = FileSystem.get(conf);
			w = SequenceFile.createWriter(fs, conf, new Path("test"),
					IntWritable.class, ArrayListOfIntsWritable.class);	
			w.append(new IntWritable(1), arr);
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<KeyValuePair<WritableComparable, Writable>> listOfKeysPairs = SequenceFileUtils.readFile("test");
		FileSystem.get(conf).delete(new Path("test"), true);

		assertTrue(listOfKeysPairs.size()==1);
		ArrayListOfIntsWritable arrRead = (ArrayListOfIntsWritable) listOfKeysPairs.get(0).getValue();
		assertTrue("got wrong: "+arrRead.size(),arrRead.size()>=4);
		assertTrue(arrRead.get(0)==1);
		assertTrue(arrRead.get(1)==3);
		assertTrue(arrRead.get(2)==5);
		assertTrue(arrRead.get(3)==7);
		
		arrRead.remove(0);
		arrRead.remove(0);
		arrRead.remove(1);

		assertTrue("got wrong: "+arrRead.size(),arrRead.size()>=1);
		assertTrue("got wrong: "+arrRead.get(0),arrRead.get(0)==5);

	}

	@Test
	public void testCopyConstructor(){
		ArrayListOfIntsWritable a = new ArrayListOfIntsWritable();
		a.add(1);
		a.add(3);
		a.add(5);
		
		ArrayListOfIntsWritable b = new ArrayListOfIntsWritable(a);
		a.remove(0);
		assertTrue(b.get(0)==1);
		assertTrue(b.get(1)==3);
		assertTrue(b.get(2)==5);		
	}
	
	@Test
	public void testIntersection(){
		ArrayListOfIntsWritable a = new ArrayListOfIntsWritable();
		a.add(5);
		a.add(3);
		a.add(1);
		
		a.trimToSize();
		Arrays.sort(a.getArray());
		
		ArrayListOfIntsWritable b = new ArrayListOfIntsWritable();
		b.add(0);
		b.add(1);
		b.add(2);
		b.add(3);
		
		ArrayListOfIntsWritable c = a.intersection(b);
		
//		System.out.println(a.size());
		assertTrue("got wrong: "+c.get(0), c.get(0)==1);
		assertTrue("got wrong: "+c.get(1), c.get(1)==3);
		
	}
	
	@Test
	public void testIntersection2(){
		ArrayListOfIntsWritable a = new ArrayListOfIntsWritable();
		a.add(5);
		
		ArrayListOfIntsWritable b = new ArrayListOfIntsWritable();
		b.add(0);
		b.add(1);
		b.add(2);
		b.add(3);
		
		ArrayListOfIntsWritable c = a.intersection(b);
		
		assertTrue(c.size()==0);
	}
	
	@Test
	public void testIntersection3(){
		ArrayListOfIntsWritable a = new ArrayListOfIntsWritable();
		a.add(3);
		a.add(5);
		a.add(7);
		a.add(8);
		a.add(9);


		ArrayListOfIntsWritable b = new ArrayListOfIntsWritable();
		b.add(0);
		b.add(1);
		b.add(2);
		b.add(3);
		
		ArrayListOfIntsWritable c = a.intersection(b);
		
		assertTrue(c.get(0)==3);
		assertTrue(c.size()==1);

	}
}
