#include "JNITest.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_JNITest_fileCopy (JNIEnv * env, jobject obj, jstring from, jstring to) 
{
	printf("%s\n", from);
	return;
}
