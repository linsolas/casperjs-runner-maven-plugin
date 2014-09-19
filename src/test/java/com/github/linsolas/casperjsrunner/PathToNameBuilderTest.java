package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.PathToNameBuilder.buildName;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.File;

public class PathToNameBuilderTest {

    @Test(expected=IllegalArgumentException.class)
    public void testBuildNameWithPathNotInRoot() {
        buildName(new File("/path/to/root/dir/"), new File("/other/path/file.ext"));
    }

    @Test
    public void testBuildName() {
        assertEquals("file_ext", buildName(new File("/path/to/root/dir/"), new File("/path/to/root/dir/file.ext")));
        assertEquals("a_file_ext", buildName(new File("/path/to/root/dir/"), new File("/path/to/root/dir/a.file.ext")));
        assertEquals("subdir_file_ext", buildName(new File("/path/to/root/dir/"), new File("/path/to/root/dir/subdir/file.ext")));
        assertEquals("subdir_a_file_ext", buildName(new File("/path/to/root/dir/"), new File("/path/to/root/dir/subdir/a.file.ext")));
    }
}
