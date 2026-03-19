eg:- 
 repo: awesome-first-repo/
			   /src
				/main
				     /hello.jva	

 then git makes it as:- 

 consider first commit as a1



					awesome-first-repo 	&larr repo(this will be taken from client)
			
						&darr

	head	&uarr		a1 		&larr actual commit (repo point to this commit)
						
						&darr

						/		&laar root tree (done this to avoid folder duplication and make it decoupled) 
						
						&darr

						src 		&larr sub tree
						
						&darr

						main 		&larr sub tree

						&darr

						hello.java	&larr the actual code blob				


 
so  this is how git/ and my mircoarchive will work.

repo will point to the commit and this will be the head.
since it is the very first commit

***PARENT Commit = NULL***


2nd commit :- a1
so head will point to this.
after repo - name the arrow will point to a2.

i.e. ***Parent Commit = a1*** 

and a2 will point to a1 from left. eg:-


						my-awesome-repo
						
						    &uarr

						    a2  &larr 	a1
								
						  //content	//a1_content



**Git commit working**
Wherever a git commits something it takes the snapshot of everything that has changed, from file to the root directory itself.

Scneario :- src/main/hello.java, hello1.java

if there is a change in hello1.java, the git will rehash hello1.java **BUT** will leave hello.java as it is .

since content is changed so, main will be rehashed, so will src.

so this new tree node will go (new tree) src-> (new tree) main -> hello1.java and git will simply draw a new arrow to hello.java

This is what git does. This saves space massively and this is why git is incredibly lightweight.
